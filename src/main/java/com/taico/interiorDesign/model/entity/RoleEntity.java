package com.taico.interiorDesign.model.entity;

import com.taico.interiorDesign.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class RoleEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Role role;


    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users = new ArrayList<>();


    public RoleEntity() {
    }


    public RoleEntity(Role role) {
        this.role = role;
    }

}