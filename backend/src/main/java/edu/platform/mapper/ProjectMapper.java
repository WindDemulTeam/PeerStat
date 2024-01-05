package edu.platform.mapper;

import edu.platform.constants.ProjectType;
import edu.platform.modelView.ProjectView;
import edu.platform.models.Project;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProjectMapper {

    private static final Map<ProjectType, String> TYPE_LOCALE = Map.of(
            ProjectType.INDIVIDUAL, "Индивидуальный",
            ProjectType.GROUP, "Групповой",
            ProjectType.INTENSIVE, "Интенсив",
            ProjectType.MODULE_PROJECT, "Модульный проект",
            ProjectType.EXAM, "Экзамен",
            ProjectType.INTERNSHIP, "Стажировка"
    );

    private static final Map<Boolean, String> MANDATORY_LOCALE = Map.of(
            Boolean.TRUE, "Обязательный",
            Boolean.FALSE, "Необязательный"
    );

    public ProjectView getProjectView(Project project) {
        ProjectView projectView = new ProjectView();
        projectView.setProjectId(project.getId());
        projectView.setNodeCode(project.getNodeCode());
        projectView.setProjectName(project.getProjectName());
        projectView.setProjectDescription(project.getProjectDescription());
        projectView.setPoints(project.getPoints());
        projectView.setDuration(project.getDuration());
        projectView.setMandatory(MANDATORY_LOCALE.get(project.getIsMandatory()));
        projectView.setType(TYPE_LOCALE.get(project.getProjectType()));
        return projectView;
    }
}
