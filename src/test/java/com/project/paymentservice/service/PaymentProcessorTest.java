package com.project.paymentservice.service;

import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.entity.Payment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PaymentProcessorTest {
    private final PaymentProcessor processor = new PaymentProcessor();

    @Test
    void processPayment_NullAmount_ReturnsFailed() {
        Payment payment = new Payment();
        assertEquals(PaymentStatus.FAILED, processor.processPayment(payment));
    }

    @Test
    void processPayment_ExceedsLimit_ReturnsFailed() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("1000000.01"));
        assertEquals(PaymentStatus.FAILED, processor.processPayment(payment));
    }

    @Test
    void processPayment_ValidAmount_ReturnsSuccess() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("500.00"));
        assertEquals(PaymentStatus.SUCCESS, processor.processPayment(payment));
    }


}
