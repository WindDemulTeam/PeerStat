package edu.platform.repository;

import edu.platform.models.User;
import edu.platform.models.UserProjectDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    User findUserByLogin(String login);

    List<User> findAll();

    List<User> findUserByCampus(String campus);

    List<User> findByOrderByXpDesc();

    List<User> findAllByLevel(int level);

    @Query(value = """
            WITH proj AS (
                SELECT users.login, array_agg(projects.project_name) AS projects_name
                FROM users
                       LEFT JOIN user_projects
                       ON users.login = user_projects.user_login
                       LEFT JOIN projects
                       ON user_projects.project_id = projects.id
                WHERE (user_projects.project_state = 'IN_PROGRESS')
                OR (user_projects.project_state = 'P2P_EVALUATIONS')
                GROUP BY  1
            )

            SELECT
                users.login as login,
                users.email as email,
                users.campus as campus,
                users.level as level,
                users.xp as xp,
                users.xp_history as xpHistory,
                users.wave_name as waveName,
                users.bootcamp_name as bootcampName,
                users.coalition_name as coalitionName,
                users.is_active as isActive,
                users.is_graduate as isGraduate,
                users.peer_points as peerPoints,
                users.code_review_points as codeReviewPoints,
                users.coins as coins,
                proj.projects_name as projects
            FROM users
            LEFT JOIN proj
            ON users.login = proj.login
            WHERE users.campus = ?1
                            """,
            nativeQuery = true)
    List<UserProjectDTO> findUserByCampusWithProjects(String campus);
}
