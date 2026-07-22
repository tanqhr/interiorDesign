package com.taico.interiorDesign.model.entity;



import com.taico.interiorDesign.enums.PaymentMethod;
import com.taico.interiorDesign.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Кой проект се заплаща
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private ProjectEntity project;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    // ID от външна платежна система (Stripe, PayPal и др.)
    @Column(unique = true)
    private String transactionId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        status = PaymentStatus.PENDING;
    }
}