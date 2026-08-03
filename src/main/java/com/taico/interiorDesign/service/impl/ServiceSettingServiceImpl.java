package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.entity.ServiceSettingEntity;
import com.taico.interiorDesign.repositories.ServiceSettingRepository;
import com.taico.interiorDesign.service.ServiceSettingService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceSettingServiceImpl implements ServiceSettingService {

    private final ServiceSettingRepository repository;

    public ServiceSettingServiceImpl(ServiceSettingRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<ServiceSettingEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ServiceType> getActiveServices() {

        return repository.findAll()
                .stream()
                .filter(ServiceSettingEntity::isActive)
                .map(ServiceSettingEntity::getServiceType)
                .toList();
    }

    @Override
    @Transactional
    public void activate(ServiceType serviceType) {

        ServiceSettingEntity setting =
                repository.findByServiceType(serviceType)
                        .orElseGet(() -> {
                            ServiceSettingEntity newSetting =
                                    new ServiceSettingEntity();

                            newSetting.setServiceType(serviceType);

                            return newSetting;
                        });

        setting.setActive(true);

        repository.save(setting);
    }

    @Override
    @Transactional
    public void deactivate(ServiceType serviceType) {

        ServiceSettingEntity setting =
                repository.findByServiceType(serviceType)
                        .orElseGet(() -> {
                            ServiceSettingEntity newSetting =
                                    new ServiceSettingEntity();

                            newSetting.setServiceType(serviceType);

                            return newSetting;
                        });

        setting.setActive(false);

        repository.save(setting);
    }

    @Override
    public boolean isActive(ServiceType serviceType) {

        return repository.findByServiceType(serviceType)
                .map(ServiceSettingEntity::isActive)
                .orElse(true);
    }

    @Override
    @Transactional
    public void initializeServices() {

        for (ServiceType type : ServiceType.values()) {

            if (repository.findByServiceType(type).isEmpty()) {

                ServiceSettingEntity setting =
                        new ServiceSettingEntity();

                setting.setServiceType(type);
                setting.setActive(true);

                repository.save(setting);
            }
        }
    }

}
