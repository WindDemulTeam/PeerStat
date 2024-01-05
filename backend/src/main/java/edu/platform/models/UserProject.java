package edu.platform.models;

import edu.platform.constants.ProjectState;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_projects")
public class UserProject implements Serializable {

    @EmbeddedId
    UserProjectKey id;

    private int score;
    private LocalDateTime finishDate;

    @Enumerated(EnumType.STRING)
    private ProjectState projectState;

    @ManyToOne
    @JoinColumn(name = "userLogin", nullable = false, insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "projectId", nullable = false, insertable = false, updatable = false)
    private Project project;
}
