package com.taico.interiorDesign.model.entity;

import com.taico.interiorDesign.enums.ProjectStatus;
import com.taico.interiorDesign.enums.RoomType;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

    @Getter
    @Setter
    @Entity
    @Table(name = "projects")
    public class ProjectEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // ======================
        // BASIC INFO
        // ======================

        @Column(nullable = false)
        private String title;

        @Column(columnDefinition = "TEXT")
        private String description;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private RoomType roomType;

        @Column(nullable = false, precision = 10, scale = 2)
        private BigDecimal budget;

        // ======================
        // STATUS
        // ======================

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ProjectStatus status;

        // ======================
        // RELATIONS
        // ======================

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private UserEntity author;

        @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ImageEntity> images = new ArrayList<>();

        @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<CommentEntity> comments = new ArrayList<>();

        @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
        private PaymentEntity payment;

        @OneToMany(
                mappedBy = "project",
                cascade = CascadeType.ALL,
                orphanRemoval = true
        )
        private List<DesignFileEntity> designs = new ArrayList<>();


        // ======================
        // AUDIT
        // ======================

        @Column(updatable = false)
        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        @PrePersist
        public void onCreate() {

            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();

            if (this.status == null) {
                this.status = ProjectStatus.NEW;
            }
        }

        @PreUpdate
        public void onUpdate() {
            this.updatedAt = LocalDateTime.now();
        }
    }




