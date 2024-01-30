package edu.platform.repository;

import edu.platform.models.ProjectUserDTO;
import edu.platform.models.User;
import edu.platform.models.UserProject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProjectRepository extends CrudRepository<UserProject, Long> {

    List<UserProject> findByProjectId(Long projectId);

    List<UserProject> findByUser(User user);

    @Query(value = """
            SELECT
                users.login as login,
                users.email as email,
                users.campus as campus,
                users.coalition_name as coalitionName,
                users.wave_name as waveName,
                users.is_active as isActive,
                users.is_graduate as isGraduate,
                users.level as level,
                users.xp as xp,
                user_projects.project_state as projectState,
                user_projects.score as score,
                users.location as location
            FROM users
            INNER JOIN user_projects
            ON users.login = user_projects.user_login
            WHERE
                ((user_projects.project_state = 'COMPLETED') OR (user_projects.project_state = 'FAILED') OR
                (user_projects.project_state = 'IN_PROGRESS' OR user_projects.project_state = 'WAITING_FOR_START') OR
                (user_projects.project_state = 'READY_TO_START') OR (user_projects.project_state = 'P2P_EVALUATIONS')) AND
                user_projects.project_id = ?1
            """, nativeQuery = true)
    List<ProjectUserDTO> findByProjectIdWithUsers(Long projectId);
}
