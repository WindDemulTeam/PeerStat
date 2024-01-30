package edu.platform.models;

import java.util.List;

public interface UserProjectDTO {
    String getLogin();

    String getEmail();

    String getCampus();

    int getLevel();

    int getXp();

    String getXpHistory();

    String getWaveName();

    String getBootcampName();

    String getCoalitionName();

    boolean getIsActive();

    boolean getIsGraduate();

    int getPeerPoints();

    int getCodeReviewPoints();

    int getCoins();

    List<String> getProjects();
}
