package com.taico.interiorDesign.repositories;

import com.taico.interiorDesign.model.entity.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<FaqEntity, Long> {

    List<FaqEntity> findByActiveTrueOrderByIdAsc();
}
