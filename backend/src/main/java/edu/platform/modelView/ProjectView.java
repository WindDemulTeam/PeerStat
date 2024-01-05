package edu.platform.modelView;

import lombok.Data;

@Data
public class ProjectView {
    private Long projectId;
    private String nodeCode;
    private String projectName;
    private String projectDescription;
    private int points;
    private int duration;
    private String mandatory;
    private String type;
}
