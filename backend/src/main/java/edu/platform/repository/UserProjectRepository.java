package edu.platform.repository;

import edu.platform.models.User;
import edu.platform.models.UserProject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProjectRepository extends CrudRepository<UserProject, Long> {

    List<UserProject> findByProjectId(Long projectId);

    List<UserProject> findByUser(User user);
}
