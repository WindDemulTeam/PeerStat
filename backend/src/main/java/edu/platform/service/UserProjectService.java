package edu.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import edu.platform.constants.ProjectState;
import edu.platform.mapper.UserProjectMapper;
import edu.platform.modelView.ProjectUserView;
import edu.platform.models.Project;
import edu.platform.models.User;
import edu.platform.models.UserProject;
import edu.platform.models.UserProjectKey;
import edu.platform.repository.UserProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static edu.platform.constants.GraphQLConstants.*;

@Service
public class UserProjectService {

    private static final List<ProjectState> ACTIVE_PROJECT_STATES = List.of(
            ProjectState.FAILED,
            ProjectState.COMPLETED,
            ProjectState.IN_PROGRESS,
            ProjectState.WAITING_FOR_START,
            ProjectState.READY_TO_START,
            ProjectState.P2P_EVALUATIONS
    );
    private UserProjectRepository userProjectRepository;
    private UserProjectMapper userProjectMapper;

    @Autowired
    public void setUserProjectRepository(UserProjectRepository userProjectRepository) {
        this.userProjectRepository = userProjectRepository;
    }

    @Autowired
    public void setUserProjectMapper(UserProjectMapper userProjectMapper) {
        this.userProjectMapper = userProjectMapper;
    }

    public List<String> getCurrentUserProjects(User user) {
        return userProjectRepository.findByUser(user).stream()
                .filter(userProject -> userProject.getProjectState().equals(ProjectState.IN_PROGRESS))
                .map(UserProject::getProject)
                .map(Project::getProjectName)
                .collect(Collectors.toList());
    }

    public List<ProjectUserView> getProjectUsersList(long projectId) {
        return userProjectRepository.findByProjectId(projectId).stream()
                .filter(this::isProjectStateActive)
                .map(userProjectMapper::getProjectUserView)
                .collect(Collectors.toList());
    }

    public void createAndSaveGoal(User user, Project project, JsonNode userProjectJson) {
        UserProject userProject = create(user, project);
        ProjectState state = ProjectState.valueOf(userProjectJson.get(GOAL_STATUS).asText());
        int score = userProjectJson.get(FINAL_PERCENTAGE).asInt();
        userProject.setProjectState(state);
        userProject.setScore(score);
        userProjectRepository.save(userProject);
    }

    public void createAndSaveCourse(User user, Project project, JsonNode userProjectJson) {
        UserProject userProject = create(user, project);
        ProjectState state = ProjectState.valueOf(userProjectJson.get(PROJECT_STATE).asText());
        userProject.setProjectState(state);
        userProjectRepository.save(userProject);
    }

    private UserProject create(User user, Project project) {
        UserProject userProject = new UserProject();
        userProject.setId(new UserProjectKey(user.getLogin(), project.getId()));
        userProject.setUser(user);
        userProject.setProject(project);
        return userProject;
    }

    private boolean isProjectStateActive(UserProject up) {
        return up.getProjectState() != null && ACTIVE_PROJECT_STATES.contains(up.getProjectState());
    }
}
