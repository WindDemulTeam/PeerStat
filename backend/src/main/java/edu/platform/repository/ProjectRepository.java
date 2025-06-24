package edu.platform.repository;

import edu.platform.constants.EntityType;
import edu.platform.models.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    List<Project> findAll();
    List<Project> findByCourseIdAndEntityTypeOrEntityType(Long courseId, EntityType entityType1, EntityType entityTyp2);
    List<Project> findByCourseIdAndEntityType(Long courseId, EntityType entityType);
}
