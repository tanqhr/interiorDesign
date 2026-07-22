package com.taico.interiorDesign.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

    @Getter
    @Setter
    @Entity
    @Table(name = "comments")
    public class CommentEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, columnDefinition = "TEXT")
        private String message;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "project_id", nullable = false)
        private ProjectEntity project;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "author_id", nullable = false)
        private UserEntity author;

        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @PrePersist
        public void onCreate() {
            createdAt = LocalDateTime.now();
        }
    }

