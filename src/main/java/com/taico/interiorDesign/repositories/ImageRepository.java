package com.taico.interiorDesign.repositories;

import com.taico.interiorDesign.model.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    List<ImageEntity> findAllByProjectId(Long projectId);

}
