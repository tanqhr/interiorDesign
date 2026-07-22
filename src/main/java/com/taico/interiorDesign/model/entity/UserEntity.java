package com.taico.interiorDesign.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    @Column(name = "username", nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean active = true;


    // ======================
    // RELATIONS
    // ======================


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<ProjectEntity> projects = new ArrayList<>();


    @OneToMany(mappedBy = "author")
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "uploadedBy")
    private List<DesignFileEntity> uploadedDesigns = new ArrayList<>();

    // ======================
    // AUDIT FIELDS
    // ======================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ======================
    // LIFECYCLE METHODS
    // ======================

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addRoles(RoleEntity userRole) {
        this.roles.add(userRole);
    }


}