package com.taico.interiorDesign.repositories;

import com.taico.interiorDesign.model.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    // всички проекти на даден user
    List<ProjectEntity> findAllByAuthorId(Long authorId);

}