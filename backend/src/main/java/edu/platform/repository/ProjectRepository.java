package edu.platform.repository;

import edu.platform.constants.EntityType;
import edu.platform.models.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    List<Project> findAll();
    List<Project> findByCourseIdAndEntityTypeOrEntityType(int courseId, EntityType entityType1, EntityType entityTyp2);
    List<Project> findByCourseIdAndEntityType(int courseId, EntityType entityType);
}
