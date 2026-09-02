package com.taico.interiorDesign.repositories;

import com.taico.interiorDesign.model.entity.NotificationEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    long countByUserAndReadFalse(UserEntity user);
}
