package edu.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.platform.constants.EntityType;
import edu.platform.constants.ProjectType;
import edu.platform.mapper.ProjectMapper;
import edu.platform.modelView.ProjectView;
import edu.platform.models.Project;
import edu.platform.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static edu.platform.constants.GraphQLConstants.*;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;
    private ProjectMapper projectMapper;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setProjectMapper(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public ProjectView getProjectInfo(long id) {
        Project project = projectRepository.findById(id).orElse(new Project());
        return projectMapper.getProjectView(project);
    }

    public List<ProjectView> getProjectListForWeb() {
        return projectRepository.findAll().stream()
                .map(projectMapper::getProjectView)
                .toList();
    }

    public void save(Project project) {
        projectRepository.save(project);
    }

    public void save(JsonNode projectJson) {
        try {
            projectRepository.save(createFromJson(projectJson));
        } catch (JsonProcessingException e) {
            System.out.println("[Project Service] can not create project " + projectJson);
            System.out.println("[Project Service]  " + e.getMessage());

        }
    }

    public Project createFromJson(JsonNode projectJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> projectMap = objectMapper.convertValue(projectJson, new TypeReference<Map<String, Object>>() {
        });

        Project project = new Project();
        project.setId(Long.parseLong(projectMap.get(ENTITY_ID).toString()));
        project.setNodeId(Integer.parseInt(projectMap.get(GRAPH_NODE_ID).toString()));
        project.setNodeCode(projectMap.get(NODE_CODE).toString());

        EntityType entityType = EntityType.valueOf(projectMap.get(ENTITY_TYPE).toString());
        project.setEntityType(entityType);

        Map<String, String> projectInfoMap = objectMapper.convertValue(projectJson.get(entityType.name().toLowerCase()), new TypeReference<Map<String, String>>() {
        });

        project.setProjectName(projectInfoMap.get(PROJECT_NAME));
        project.setProjectDescription(projectInfoMap.get(PROJECT_DESCRIPTION));
        project.setPoints(Integer.parseInt(projectInfoMap.get(PROJECT_POINTS)));
        project.setDuration(Integer.parseInt(projectInfoMap.get(DURATION)));

        if (entityType.equals(EntityType.GOAL)) {
            project.setProjectType(ProjectType.valueOf(projectInfoMap.get(GOAL_TYPE)));
            project.setIsMandatory(Boolean.valueOf(projectInfoMap.get(IS_MANDATORY)));
        } else if (entityType.equals(EntityType.COURSE)) {
            project.setProjectType(ProjectType.valueOf(projectInfoMap.get(COURSE_TYPE)));
            project.setCourseId(Integer.parseInt(projectInfoMap.get(COURSE_ID)));
            project.setIsMandatory(Boolean.valueOf(projectInfoMap.get(IS_MANDATORY)));
        }

        return project;
    }
}
