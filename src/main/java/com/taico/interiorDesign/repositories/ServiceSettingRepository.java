package com.taico.interiorDesign.repositories;

import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.entity.ServiceSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceSettingRepository extends JpaRepository<ServiceSettingEntity, Long> {

    Optional<ServiceSettingEntity> findByServiceType(ServiceType serviceType);
}
