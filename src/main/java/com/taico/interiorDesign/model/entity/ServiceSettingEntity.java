package com.taico.interiorDesign.model.entity;

import com.taico.interiorDesign.enums.ServiceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_settings")
@Getter
@Setter
public class ServiceSettingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ServiceType serviceType;

    @Column(nullable = false)
    private boolean active = true;
}
