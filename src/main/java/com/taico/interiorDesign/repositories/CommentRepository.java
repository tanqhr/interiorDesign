package com.taico.interiorDesign.repositories;


import com.taico.interiorDesign.model.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findAllByProjectIdOrderByCreatedAtAsc(Long projectId);

}