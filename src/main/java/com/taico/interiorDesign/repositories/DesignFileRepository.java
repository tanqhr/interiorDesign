package com.taico.interiorDesign.repositories;


import com.taico.interiorDesign.model.entity.DesignFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignFileRepository
        extends JpaRepository<DesignFileEntity, Long> {

    Optional<DesignFileEntity> findByProjectId(Long projectId);
    List<DesignFileEntity> findAllByProjectId(Long projectId);


}