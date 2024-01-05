package edu.platform.parser;

import com.fasterxml.jackson.databind.JsonNode;
import edu.platform.constants.EntityType;
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

import static edu.platform.constants.GraphQLConstants.*;

@Component
public class Parser {
    private static final int SEARCH_LIMIT = 25;
    private static final String LAST_UPDATE_PROPERTIES_FILE = "last-update.properties";
    private static final String LAST_UPDATE_TIME = "task-update.time";

    private UserService userService;
    private ProjectService projectService;
    private UserProjectService userProjectService;
    private LoginService loginService;

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
                userList.stream()
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

        List<User> usersList = userService.getAll();
        for (User user : usersList) {
            try {
                setCredentials(user);
                setPersonalInfo(user);
                setXpHistory(user);
                userService.save(user);

                setUserProjects(user);
                setUserProjectsFromGraph(user);

                System.out.println("[parser updateUsers] user " + user.getLogin() + " ok");
            } catch (Exception e) {
                System.out.println("[parser updateUsers] ERROR " + user.getLogin() + " " + e.getMessage());
            }
        }

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
                setUserProjectsFromGraph(user);

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

    private void setUserProjectsFromGraph(User user) throws IOException {
        JsonNode graphInfo = sendRequest(RequestBody.getGraphInfo(user));
        if (!graphInfo.isEmpty()) {
            JsonNode projectsListJson = graphInfo.get(STUDENT).get(BASIC_GRAPH).get(GRAPH_NODES);
            for (JsonNode projectJson : projectsListJson) {
                EntityType entityType = EntityType.valueOf(projectJson.get(ENTITY_TYPE).asText());
                if (entityType.equals(EntityType.COURSE)) {
                    String stateStr = projectJson.get(COURSE).get(PROJECT_STATE).asText();
                    ProjectState projectState = stateStr == null ? null : ProjectState.valueOf(stateStr);
                    if (projectState != null && !ProjectState.LOCKED.equals(projectState)) {
                        Long projectId = projectJson.get(ENTITY_ID).asLong();
                        Optional<Project> projectOpt = projectService.findById(projectId);
                        if (projectOpt.isPresent()) {
                            userProjectService.createAndSaveCourse(user, projectOpt.get(), projectJson.get(COURSE));
                        } else {
                            System.out.println("[PARSER] ERROR Project not found, id" + projectId);
                        }
                    }
                }
            }
        }
    }

    private void setUserProjects(User user) throws IOException {
        JsonNode userProjectInfo = sendRequest(RequestBody.getUserProjects(user));
        if (!userProjectInfo.isEmpty()) {
            JsonNode userProjectListJson = userProjectInfo.get("school21").get(STUDENT_PROJECT);
            for (JsonNode userProjectJson : userProjectListJson) {
                ProjectState projectState = ProjectState.valueOf(userProjectJson.get(GOAL_STATUS).asText());
                if (!ProjectState.UNAVAILABLE.equals(projectState)) {
                    Long projectId = Long.parseLong(userProjectJson.get(GOAL_ID).asText());
                    Optional<Project> projectOpt = projectService.findById(projectId);
                    if (projectOpt.isPresent()) {
                        userProjectService.createAndSaveGoal(user, projectOpt.get(), userProjectJson);
                    } else {
                        System.out.println("[PARSER] ERROR Project not found, id" + projectId);
                    }
                }
            }
        }
    }

    public void parseGraphInfo() throws IOException {
        System.out.println("[parser parseGraphInfo] begin");

        String userLogin = loginService.getLogin();
        User user = userService.findUserByLogin(userLogin);
        if (user == null) {
            user = new User(userLogin);
            setCredentials(user);
        }

        JsonNode graphInfo = sendRequest(RequestBody.getGraphInfo(user));

        if (!graphInfo.isEmpty()) {
            JsonNode projectsListJson = graphInfo.get(STUDENT).get(BASIC_GRAPH).get(GRAPH_NODES);
            projectsListJson.forEach(projectJson -> projectService.save(projectJson));
        }
        System.out.println("[parser parseGraphInfo] done");
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
            JsonNode profiles = searchInfo.get(GLOBAL_SEARCH).get(SEARCH_BY_TEXT).get(PROFILES).get(PROFILES);
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
