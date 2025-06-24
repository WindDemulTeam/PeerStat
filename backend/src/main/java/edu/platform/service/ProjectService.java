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
import jakarta.persistence.EnumType;
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

    public List<Project> findCourseById(Long courseId) {
        return projectRepository.findByCourseIdAndEntityType(courseId, EntityType.GOAL);
    }

    public ProjectView getProjectInfo(long id) {
        Project project = projectRepository.findById(id).orElse(new Project());
        return projectMapper.getProjectView(project);
    }

    public List<ProjectView> getProjectListForWeb() {
        return projectRepository.findByCourseIdAndEntityTypeOrEntityType(0L, EntityType.GOAL, EntityType.COURSE).stream()
                .map(projectMapper::getProjectView)
                .toList();
    }

    public List<ProjectView> getCourseListForWeb(Long id) {
        return projectRepository.findByCourseIdAndEntityType(id, EntityType.GOAL).stream()
                .map(projectMapper::getProjectView)
                .toList();
    }

    public void saveProject(JsonNode projectJson) {
        try {
            Project project = createProjectFromJson(projectJson);
            projectRepository.save(project);
            System.out.println("[parseProject] " + project.getProjectName());
        } catch (JsonProcessingException e) {
            System.out.println("[Project Service] can not create project " + projectJson);
            System.out.println("[Project Service]  " + e.getMessage());
        }
    }

    public Project createProjectFromJson(JsonNode projectJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> projectMap = objectMapper.convertValue(projectJson.get(MODULE_BY_ID), new TypeReference<Map<String, Object>>() {
        });

        Project project = new Project();

        project.setId(Long.parseLong(projectMap.get(PROJECT_ID).toString()));
        project.setProjectName(projectMap.get(PROJECT_TITLE).toString());
        project.setProjectType(ProjectType.valueOf(projectMap.get(GOAL_TYPE).toString()));
        project.setEntityType(EntityType.GOAL);
        Map<String, Object> projectInfoMap = objectMapper.convertValue(projectJson.get(MODULE_BY_ID).get(STUDY_MODULE), new TypeReference<Map<String, Object>>() {
        });

        project.setProjectDescription(projectInfoMap.get(PROJECT_IDEA).toString());
        project.setPoints(Integer.parseInt(projectInfoMap.get(GOAL_POINT).toString()));
        project.setDuration(Integer.parseInt(projectInfoMap.get(DURATION).toString()));
        project.setCourseId(0L);

        return project;
    }

    public void saveCourse(JsonNode courseJson, Long courseId) {
        Project project = new Project();

        JsonNode globalCourse = courseJson.get(COURSE_GOAL);

        project.setId(globalCourse.get(GLOBAL_COURSE_ID).asLong());
        project.setCourseId(courseId);
        project.setEntityType(EntityType.COURSE);
        project.setProjectName(globalCourse.get(GLOBAL_COURSE_NAME).asText());

        ProjectType projectType = ProjectType.valueOf(globalCourse.get(COURSE_TYPE).asText());
        project.setProjectType(projectType);
        projectRepository.save(project);
        System.out.println("[parseCourse] " + project.getProjectName());

        JsonNode localCourses = globalCourse.get(LOCAL_COURSE_GOAL);
        for (JsonNode course : localCourses) {
            project = new Project();
            project.setId(course.get(GOAL_ID).asLong());
            project.setCourseId(courseId);
            project.setProjectName(course.get(LOCAL_COURSE_NAME).asText());
            project.setProjectDescription(course.get(LOCAL_COURSE_DESCRIPTION).asText());
            project.setDuration(course.get(LOCAL_COURSE_DURATION).asInt());
            project.setPoints(course.get(LOCAL_COURSE_SCORE).asInt());
            project.setEntityType(EntityType.GOAL);

            projectType = ProjectType.valueOf(course.get(LOCAL_COURSE_TYPE).asText());
            project.setProjectType(projectType);
            projectRepository.save(project);
            System.out.println("[parseCourse] " + project.getProjectName());
        }
    }
}
