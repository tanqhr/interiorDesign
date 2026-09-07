package com.taico.interiorDesign.service.impl;


import com.taico.interiorDesign.enums.ServiceType;
import com.taico.interiorDesign.model.entity.ServiceSettingEntity;
import com.taico.interiorDesign.repositories.ServiceSettingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceSettingServiceImplTest {

    @Mock
    private ServiceSettingRepository repository;

    @InjectMocks
    private ServiceSettingServiceImpl service;

    @Test
    void findAll_shouldReturnAllSettings() {

        // Arrange
        ServiceSettingEntity setting1 = createSetting(
                ServiceType.INTERIOR_DESIGN,
                true
        );

        ServiceSettingEntity setting2 = createSetting(
                ServiceType.DESIGN_CONSULTATION,
                false
        );

        List<ServiceSettingEntity> settings =
                List.of(setting1, setting2);

        when(repository.findAll()).thenReturn(settings);

        // Act
        List<ServiceSettingEntity> result = service.findAll();

        // Assert
        assertEquals(settings, result);

        verify(repository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoSettingsExist() {

        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<ServiceSettingEntity> result = service.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll();
    }

    @Test
    void getActiveServices_shouldReturnOnlyActiveServices() {

        // Arrange
        ServiceSettingEntity activeSetting =
                createSetting(
                        ServiceType.INTERIOR_DESIGN,
                        true
                );

        ServiceSettingEntity inactiveSetting =
                createSetting(
                        ServiceType.DESIGN_CONSULTATION,
                        false
                );

        when(repository.findAll())
                .thenReturn(List.of(
                        activeSetting,
                        inactiveSetting
                ));

        // Act
        List<ServiceType> result =
                service.getActiveServices();

        // Assert
        assertEquals(
                List.of(ServiceType.INTERIOR_DESIGN),
                result
        );

        verify(repository).findAll();
    }

    @Test
    void getActiveServices_shouldReturnEmptyList_whenNoServicesAreActive() {

        // Arrange
        ServiceSettingEntity setting1 =
                createSetting(
                        ServiceType.INTERIOR_DESIGN,
                        false
                );

        ServiceSettingEntity setting2 =
                createSetting(
                        ServiceType.DESIGN_CONSULTATION,
                        false
                );

        when(repository.findAll())
                .thenReturn(List.of(setting1, setting2));

        // Act
        List<ServiceType> result =
                service.getActiveServices();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll();
    }

    @Test
    void activate_shouldActivateExistingSetting() {

        // Arrange
        ServiceType serviceType =
                ServiceType.INTERIOR_DESIGN;

        ServiceSettingEntity setting =
                createSetting(serviceType, false);

        when(repository.findByServiceType(serviceType))
                .thenReturn(Optional.of(setting));

        // Act
        service.activate(serviceType);

        // Assert
        assertTrue(setting.isActive());

        verify(repository).findByServiceType(serviceType);
        verify(repository).save(setting);
    }

    @Test
    void activate_shouldCreateAndActivateSetting_whenSettingDoesNotExist() {

        // Arrange
        ServiceType serviceType =
                ServiceType.INTERIOR_DESIGN;

        when(repository.findByServiceType(serviceType))
                .thenReturn(Optional.empty());

        // Act
        service.activate(serviceType);

        // Assert
        ArgumentCaptor<ServiceSettingEntity> captor =
                ArgumentCaptor.forClass(ServiceSettingEntity.class);

        verify(repository).save(captor.capture());

        ServiceSettingEntity savedSetting =
                captor.getValue();

        assertEquals(
                serviceType,
                savedSetting.getServiceType()
        );

        assertTrue(savedSetting.isActive());
    }

    @Test
    void deactivate_shouldDeactivateExistingSetting() {

        // Arrange
        ServiceType serviceType =
                ServiceType.INTERIOR_DESIGN;

        ServiceSettingEntity setting =
                createSetting(serviceType, true);

        when(repository.findByServiceType(serviceType))
                .thenReturn(Optional.of(setting));

        // Act
        service.deactivate(serviceType);

        // Assert
        assertFalse(setting.isActive());

        verify(repository).findByServiceType(serviceType);
        verify(repository).save(setting);
    }

    @Test
    void deactivate_shouldCreateAndDeactivateSetting_whenSettingDoesNotExist() {

        // Arrange
        ServiceType serviceType =
                ServiceType.INTERIOR_DESIGN;

        when(repository.findByServiceType(serviceType))
                .thenReturn(Optional.empty());

        // Act
        service.deactivate(serviceType);

        // Assert
        ArgumentCaptor<ServiceSettingEntity> captor =
                ArgumentCaptor.forClass(ServiceSettingEntity.class);

        verify(repository).save(captor.capture());

        ServiceSettingEntity savedSetting =
                captor.getValue();

        assertEquals(
                serviceType,
                savedSetting.getServiceType()
        );

        assertFalse(savedSetting.isActive());
    }

    @Test
    void isActive_shouldReturnActualStatus_whenSettingExists() {

        // Arrange
        ServiceType serviceType =
                ServiceType.INTERIOR_DESIGN;

        ServiceSettingEntity setting =
                createSetting(serviceType, true);

        when(repository.findByServiceType(serviceType))
                .thenReturn(Optional.of(setting));

        // Act
        boolean result =
                service.isActive(serviceType);

        // Assert
        assertTrue(result);

        verify(repository).findByServiceType(serviceType);
    }

    @Test
    void isActive_shouldReturnFalse_whenExistingSettingIsInactive() {

        // Arrange
        ServiceType serviceType =
                ServiceType.INTERIOR_DESIGN;

        ServiceSettingEntity setting =
                createSetting(serviceType, false);

        when(repository.findByServiceType(serviceType))
                .thenReturn(Optional.of(setting));

        // Act
        boolean result =
                service.isActive(serviceType);

        // Assert
        assertFalse(result);

        verify(repository).findByServiceType(serviceType);
    }

    @Test
    void isActive_shouldReturnTrue_whenSettingDoesNotExist() {

        // Arrange
        ServiceType serviceType =
                ServiceType.INTERIOR_DESIGN;

        when(repository.findByServiceType(serviceType))
                .thenReturn(Optional.empty());

        // Act
        boolean result =
                service.isActive(serviceType);

        // Assert
        assertTrue(result);

        verify(repository).findByServiceType(serviceType);
    }

    @Test
    void initializeServices_shouldCreateMissingServices() {

        // Arrange
        for (ServiceType type : ServiceType.values()) {
            when(repository.findByServiceType(type))
                    .thenReturn(Optional.empty());
        }

        // Act
        service.initializeServices();

        // Assert
        verify(repository, times(ServiceType.values().length))
                .save(any(ServiceSettingEntity.class));
    }

    @Test
    void initializeServices_shouldNotCreateExistingServices() {

        // Arrange
        for (ServiceType type : ServiceType.values()) {

            ServiceSettingEntity existingSetting =
                    createSetting(type, true);

            when(repository.findByServiceType(type))
                    .thenReturn(Optional.of(existingSetting));
        }

        // Act
        service.initializeServices();

        // Assert
        verify(repository, never())
                .save(any(ServiceSettingEntity.class));
    }

    private ServiceSettingEntity createSetting(
            ServiceType serviceType,
            boolean active) {

        ServiceSettingEntity setting =
                new ServiceSettingEntity();

        setting.setServiceType(serviceType);
        setting.setActive(active);

        return setting;
    }
}
