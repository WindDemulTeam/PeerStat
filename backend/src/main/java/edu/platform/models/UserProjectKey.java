package edu.platform.models;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@Embeddable
public class UserProjectKey implements Serializable {
    private String userLogin;
    private Long projectId;

    public UserProjectKey(String userLogin, Long projectId) {
        this.userLogin = userLogin;
        this.projectId = projectId;
    }

    public UserProjectKey() {
    }
}
