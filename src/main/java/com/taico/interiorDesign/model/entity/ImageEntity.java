package com.taico.interiorDesign.model.entity;


    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;


    import java.time.LocalDateTime;

    @Getter
    @Setter
    @Entity
    @Table(name = "images")
    public class ImageEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String fileName;

        @Column(nullable = false)
        private String filePath;

        @Column(nullable = false)
        private String contentType;

        @Column(nullable = false)
        private Long fileSize;

        @Column(nullable = false, updatable = false)
        private LocalDateTime uploadedAt;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "project_id", nullable = false)
        private ProjectEntity project;

        @PrePersist
        public void onCreate() {
            uploadedAt = LocalDateTime.now();
        }
    }
