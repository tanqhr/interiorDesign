package com.taico.interiorDesign.repositories;

import com.taico.interiorDesign.model.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByTransactionId(String transactionId);

}
