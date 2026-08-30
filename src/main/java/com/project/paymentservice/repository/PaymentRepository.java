package com.project.paymentservice.repository;

import com.project.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPayerId(String payerId);
    Optional<Payment> findByPayerName(String payerName);
}
