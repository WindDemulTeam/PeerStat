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

    public List<Project> findCourseById(int courseId) {
        return projectRepository.findByCourseIdAndEntityType(courseId, EntityType.GOAL);
    }

    public ProjectView getProjectInfo(long id) {
        Project project = projectRepository.findById(id).orElse(new Project());
        return projectMapper.getProjectView(project);
    }

    public List<ProjectView> getProjectListForWeb() {
        return projectRepository.findByCourseIdAndEntityTypeOrEntityType(0, EntityType.GOAL, EntityType.COURSE).stream()
                .map(projectMapper::getProjectView)
                .toList();
    }
    public List<ProjectView> getCourseListForWeb(int id) {
        return projectRepository.findByCourseIdAndEntityType(id, EntityType.GOAL).stream()
                .map(projectMapper::getProjectView)
                .toList();
    }

    public void save(JsonNode projectJson, JsonNode courseJson) {
        try {
            Project project = createProjectFromJson(projectJson);
            if (courseJson != null && !courseJson.isNull()) {
                int courseId = courseJson.get(COURSE).get(COURSE_GOAL).get(COURSE_ID).asInt();
                JsonNode courses = courseJson.get(COURSE).get(COURSE_GOAL).get(LOCAL_COURSE_GOAL);
                project.setCourseId(courseId);
                for (JsonNode course :
                        courses) {
                    Project projectCourse = createCourseFromJson(course, courseId);
                    if (!projectCourse.getProjectType().equals(ProjectType.EXAM_TEST)) {
                        System.out.println(projectCourse.getProjectName());
                        projectRepository.save(projectCourse);
                    }
                }
            }
            System.out.println(project.getProjectName());
            projectRepository.save(project);
        } catch (JsonProcessingException e) {
            System.out.println("[Project Service] can not create project " + projectJson);
            System.out.println("[Project Service]  " + e.getMessage());

        }
    }

    public Project createProjectFromJson(JsonNode projectJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> projectMap = objectMapper.convertValue(projectJson, new TypeReference<Map<String, Object>>() {
        });

        Project project = new Project();
        project.setId(Long.parseLong(projectMap.get(ENTITY_ID).toString()));
        project.setCode(projectMap.get(CODE).toString());

        EntityType entityType = EntityType.valueOf(projectMap.get(ENTITY_TYPE).toString());
        project.setEntityType(entityType);

        Map<String, String> projectInfoMap = objectMapper.convertValue(projectJson.get(entityType.name().toLowerCase()), new TypeReference<Map<String, String>>() {
        });

        project.setProjectName(projectInfoMap.get(PROJECT_NAME));
        project.setProjectDescription(projectInfoMap.get(PROJECT_DESCRIPTION));
        project.setPoints(Integer.parseInt(projectInfoMap.get(PROJECT_POINTS)));

        if (entityType.equals(EntityType.GOAL)) {
            project.setProjectType(ProjectType.valueOf(projectInfoMap.get(GOAL_TYPE)));
            project.setIsMandatory(Boolean.valueOf(projectInfoMap.get(IS_MANDATORY)));
        } else if (entityType.equals(EntityType.COURSE)) {
            ProjectType projectType = ProjectType.valueOf(projectInfoMap.get(COURSE_TYPE));
            project.setProjectType(projectType);
            project.setIsMandatory(Boolean.valueOf(projectInfoMap.get(IS_MANDATORY)));
        }

        return project;
    }

    public Project createCourseFromJson(JsonNode courseJson, int courseId) throws JsonProcessingException {

        Project project = new Project();

        project.setId(courseJson.get(GOAL_ID).asLong());
        project.setCourseId(courseId);
        project.setEntityType(EntityType.GOAL);
        project.setProjectName(courseJson.get(LOCAL_COURSE_NAME).asText());
        project.setProjectDescription(courseJson.get(LOCAL_COURSE_DESCRIPTION).asText());

        ProjectType projectType = ProjectType.valueOf(courseJson.get(LOCAL_COURSE_TYPE).asText());
        project.setProjectType(projectType);
        project.setIsMandatory(true);

        return project;
    }
}
