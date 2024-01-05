package edu.platform.modelView;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProjectUserView {
    private String login;
    private String email;
    private String campus;
    private String coalition;
    private String wave;
    private String platformClass;
    private int level;
    private int xp;
    private String state;
    private int score;
    private String location;
}
