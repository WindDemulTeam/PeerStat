package edu.platform.models;

import edu.platform.constants.EntityType;
import edu.platform.constants.ProjectType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    private Long id;
    private int nodeId;
    private String nodeCode;
    private Boolean isMandatory;
    private int courseId;
    private String projectName;
    private int points;
    private int duration;

    private String subjectLink;
    private String accessAfter;
    private String restartBefore;

    @Column(columnDefinition = "TEXT")
    private String projectDescription;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Enumerated(EnumType.STRING)
    private ProjectType projectType;

    @ToString.Exclude
    @OneToMany(mappedBy = "project")
    private List<UserProject> userProjectList;
}
