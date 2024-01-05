package edu.platform.modelView;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class StatUserView {
    private String login;
    private String email;
    private String campus;
    private String coalition;
    private String wave;
    private String platformClass;
    private String bootcamp;
    private int level;
    private int xp;
    private int peerPoints;
    private int codeReviewPoints;
    private int coins;
    private int diff;
    private int diff3;
    private String currentProject;
}
