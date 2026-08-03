package com.taico.interiorDesign.service;

import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.entity.ServiceSettingEntity;

import java.util.List;

public interface ServiceSettingService {

    List<ServiceSettingEntity> findAll();

    void activate(ServiceType serviceType);

    void deactivate(ServiceType serviceType);

    boolean isActive(ServiceType serviceType);

    void initializeServices();

    List<ServiceType> getActiveServices();
}
