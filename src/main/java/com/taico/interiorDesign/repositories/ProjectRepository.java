package com.taico.interiorDesign.repositories;

import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    // всички проекти на даден user
    List<ProjectEntity> findAllByAuthorId(Long authorId);

    List<ProjectEntity> findByAuthor(UserEntity author);



}