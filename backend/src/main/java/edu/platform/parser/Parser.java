package edu.platform.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.platform.constants.ProjectState;
import edu.platform.models.Project;
import edu.platform.models.User;
import edu.platform.service.LoginService;
import edu.platform.service.ProjectService;
import edu.platform.service.UserProjectService;
import edu.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

import static edu.platform.constants.GraphQLConstants.*;

@Component
public class Parser {
    private static final int SEARCH_LIMIT = 6;
    private static final String LAST_UPDATE_PROPERTIES_FILE = "last-update.properties";
    private static final String LAST_UPDATE_TIME = "task-update.time";

    private UserService userService;
    private ProjectService projectService;
    private UserProjectService userProjectService;
    private LoginService loginService;

    private final ForkJoinPool userUpdatePool = new ForkJoinPool(20);
    private final ForkJoinPool courseUpdatePool = new ForkJoinPool(12);
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setUserProjectService(UserProjectService userProjectService) {
        this.userProjectService = userProjectService;
    }

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    public void initUsers() {
        System.out.println("[parser initUsers] initUsers by login " + loginService.getLogin());

        List<String> currentUsersList = userService.getAll().stream()
                .map(User::getLogin).toList();

        int offset = 0;
        try {
            List<User> userList = getSearchResults(offset);
            while (!userList.isEmpty()) {
                userList.parallelStream()
                        .filter(user -> !currentUsersList.contains(user.getLogin()))
                        .forEach(this::parseNewUser);

                offset += SEARCH_LIMIT;
                userList = getSearchResults(offset);
            }

            setLastUpdateTime();

        } catch (IOException e) {
            System.out.println("[parser initUsers] ERROR offset " + offset + " " + e.getMessage());
        }
    }

    public void updateUsers() {
        System.out.println("[parser updateUsers] updateUsers by login " + loginService.getLogin());

        userUpdatePool.submit(() -> {
            userService.getAll().parallelStream().forEach((user) -> {
                try {
                    setCredentials(user);
                    setPersonalInfo(user);
                    setXpHistory(user);
                    userService.save(user);

                    setUserProjects(user);

                    System.out.println("[parser updateUsers] user " + user.getLogin() + " ok");
                } catch (Exception e) {
                    System.out.println("[parser updateUsers] ERROR " + user.getLogin() + " " + e.getMessage());
                }
            });
        }).join();

        System.out.println("[parser updateUsers] done " + LocalDateTime.now());
        setLastUpdateTime();
    }

    private void parseNewUser(User user) {
        try {
            setCredentials(user);
            setPersonalInfo(user);

            if (CORE_PROGRAM.equals(user.getEduForm())) {
                setCoalitionInfo(user);
                setStageInfo(user);
                setXpHistory(user);
                userService.save(user);

                setUserProjects(user);

                System.out.println("[parseUser] user done " + user.getLogin());
            } else {
                System.out.println("[parseUser] user skipped " + user.getLogin());
            }
        } catch (Exception e) {
            System.out.println("[parseUser] ERROR " + user.getLogin() + " " + e.getMessage());
        }
    }

    private void setCredentials(User user) throws IOException {
        JsonNode response = sendRequest(RequestBody.getCredentialInfo(user));
        userService.setCredentials(user, response);
    }

    private void setPersonalInfo(User user) throws IOException {
        userService.setPersonalInfo(user, sendRequest(RequestBody.getPersonalInfo(user)));
    }

    private void setCoalitionInfo(User user) throws IOException {
        userService.setCoalitionInfo(user, sendRequest(RequestBody.getCoalitionInfo(user)));
    }

    private void setStageInfo(User user) throws IOException {
        userService.setStageInfo(user, sendRequest(RequestBody.getStageInfo(user)));
    }

    private void setXpHistory(User user) throws IOException {
        userService.setXpHistory(user, sendRequest(RequestBody.getXpHistory(user)));
    }

    private void setUserCourses(User user, List<Project> courseList) {
        courseUpdatePool.submit(() -> {
            courseList.parallelStream().forEach((course) -> {
                try {
                    JsonNode courseJson = sendRequest(RequestBody.getProjectInfoByStudent(user, course.getId().intValue()));
                    ProjectState state = ProjectState.valueOf(courseJson.get(SCHOOL_21).get(MODULE_BY_ID).get(MODULE_GOAL_STATUS).asText());
                    int score = courseJson.get(SCHOOL_21).get(MODULE_BY_ID).get(LOCAL_COURSE_SCORE).asInt();
                    userProjectService.createAndSaveCourse(user, course, state, score);
                } catch (IOException e) {
                    System.out.println("[parseCourse] ERROR Course not found, id" + course.getId());
                }
            });
        });
    }

    private void setProjectNode(JsonNode userProjectJson, ObjectNode project, String id) {
        JsonNode param = userProjectJson.get(id);
        if (!param.isNull()) {
            project.put(id, param.asText());
        } else {
            project.putNull(id);
        }
    }

    private void setUserProjects(User user) throws IOException {
        JsonNode userProjectInfo = sendRequest(RequestBody.getUserProjects(user));
        if (!userProjectInfo.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode projects = mapper.createArrayNode();
            JsonNode userProjectListJson = userProjectInfo.get(SCHOOL_21).get(STUDENT_PROJECT);
            for (JsonNode userProjectJson : userProjectListJson) {
                ObjectNode project = projects.addObject();
                setProjectNode(userProjectJson, project, GOAL_ID);
                setProjectNode(userProjectJson, project, LOCAL_COURSE_ID);
            }
            JsonNode userProjectStatuses = sendRequest(RequestBody.getUserProjectsStatuses(user, projects));
            userProjectListJson = userProjectStatuses.get(SCHOOL_21).get(STUDENT_PROJECT_STATUS);

            for (JsonNode userProjectJson : userProjectListJson) {
                if (!ProjectState.UNAVAILABLE.equals(userProjectJson.get(GOAL_STATUS).asText())) {
                    JsonNode courseId = userProjectJson.get(COURSE_ID);
                    if (!courseId.isNull()) {
                        Long id = courseId.asLong();
                        List<Project> courses = projectService.findCourseById(id);
                        if (courses.isEmpty()) {
                            if (!parseCourse(id)) {
                                continue;
                            }
                            courses = projectService.findCourseById(id);
                        }
                        for (Project course : courses) {
                            userProjectService.createAndSaveGoal(user, course, userProjectJson);
                        }
                        setUserCourses(user, courses);
                    } else {
                        Long projectId = Long.parseLong(userProjectJson.get(GOAL_ID).asText());
                        Optional<Project> projectOpt = projectService.findById(projectId);
                        if (!projectOpt.isPresent()) {
                            if (!parseProject(projectId)) {
                                continue;
                            }
                            projectOpt = projectService.findById(projectId);
                        }
                        userProjectService.createAndSaveGoal(user, projectOpt.get(), userProjectJson);
                    }
                }
            }
        }
    }

    public boolean parseProject(Long projectId) throws IOException {
        JsonNode projectInfo = sendRequest(RequestBody.getProjectInfo(projectId));
        if (!projectInfo.isEmpty()) {
            JsonNode projectJson = projectInfo.get(STUDENT);
            if (!projectJson.isNull()) {
                projectService.saveProject(projectJson);
                return true;
            }
        }
        return false;
    }

    public boolean parseCourse(Long courseId) throws IOException {
        JsonNode courseInfo = sendRequest(RequestBody.getLocalCourseGoals(courseId));
        if (!courseInfo.isEmpty()) {
            JsonNode courseJson = courseInfo.get(COURSE);
            if (!courseJson.isNull()) {
                projectService.saveCourse(courseJson, courseId);
                return true;
            }
        }
        return false;
    }

    public void updateUserLocations() {
        try {
            Map<Integer, String> clustersMap = new HashMap<>();
            Map<String, String> currentLocationsMap = new HashMap<>();

            JsonNode buildingInfo = loginService.sendRequest(RequestBody.getBuildingInfo());
            if (buildingInfo != null) {
                JsonNode buildingsList = buildingInfo.get("student").get("getBuildings");
                for (JsonNode building : buildingsList) {
                    JsonNode clustersList = building.get("classrooms");
                    for (JsonNode cluster : clustersList) {
                        Integer clusterId = cluster.get("id").asInt();
                        String clusterName = cluster.get("number").asText();
                        clustersMap.put(clusterId, clusterName);
                    }
                }
            } else {
                System.out.println("[updateUserLocations] buildingInfo NULL");
            }

            for (Integer clusterId : clustersMap.keySet()) {
                JsonNode clusterPlanInfo = loginService.sendRequest(RequestBody.getClusterPlanInfo(clusterId));
                if (clusterPlanInfo != null) {
                    JsonNode placesList = clusterPlanInfo.get("student").get("getClusterPlanStudentsByClusterId").get("occupiedPlaces");
                    for (JsonNode place : placesList) {
                        String location = clustersMap.get(clusterId) + " "
                                + place.get("row").asText() + "-"
                                + place.get("number").asInt();
                        String userLogin = place.get("user").get("login").asText();
                        currentLocationsMap.put(userLogin.substring(0, userLogin.indexOf("@")), location);
                    }
                }
            }
            userService.updateUsersLocation(currentLocationsMap);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> getSearchResults(int offset) throws IOException {
        List<User> userList = new ArrayList<>();
        JsonNode searchInfo = sendRequest(RequestBody.getSearchResults(SEARCH_LIMIT, offset));
        if (!searchInfo.isEmpty()) {
            JsonNode globalSearch = searchInfo.get(GLOBAL_SEARCH);
            if (globalSearch != null && !globalSearch.isNull()) {
                JsonNode profiles = globalSearch.get(SEARCH_BY_TEXT).get(PROFILES).get(PROFILES);
                for (JsonNode profile : profiles) {
                    String fullLogin = profile.get(LOGIN).asText();
                    String login = fullLogin.contains("@") ? fullLogin.substring(0, fullLogin.indexOf("@")) : fullLogin;
                    String campus = profile.get(SCHOOL).get(SHORT_NAME).asText();
                    User user = new User(login);
                    user.setCampus(campus);
                    user.setSchoolId(loginService.getSchoolId());
                    userList.add(user);
                }
            }
        }
        return userList;
    }

    private JsonNode sendRequest(String requestBody) throws IOException {
        return loginService.sendRequest(requestBody);
    }

    public String getLastUpdateTime() {
        String lastUpdateTime = "";

        try {
            Properties props = new Properties();
            DefaultPropertiesPersister p = new DefaultPropertiesPersister();
            p.load(props, new FileInputStream(LAST_UPDATE_PROPERTIES_FILE));

            lastUpdateTime = props.getProperty(LAST_UPDATE_TIME);

        } catch (IOException e) {
            System.out.println("[PARSER] ERROR " + e.getMessage());
        }

        return lastUpdateTime;
    }

    public void setLastUpdateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String lastUpdateTime = LocalDateTime.now().format(formatter);

        try {
            Properties props = new Properties();
            props.setProperty(LAST_UPDATE_TIME, lastUpdateTime);

            DefaultPropertiesPersister p = new DefaultPropertiesPersister();
            p.store(props, new FileOutputStream(LAST_UPDATE_PROPERTIES_FILE), "parser last update time");

        } catch (Exception e) {
            System.out.println("[PARSER] ERROR " + e.getMessage());
        }
    }

}
