package com.project.paymentservice.service;


import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseCreatedDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.entity.Payment;
import com.project.paymentservice.exception.InvalidPaymentException;
import com.project.paymentservice.exception.PaymentNotFoundException;
import com.project.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository repository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_Success() throws InvalidPaymentException {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setPayerName("Test User");
        request.setUpiId("test@upi");
        request.setAmount(new java.math.BigDecimal("1000.00"));

        PaymentResponseCreatedDto response = paymentService.createPayment(request);

        assertNotNull(response.getPaymentId());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        verify(repository, times(1)).save(any(Payment.class));
    }

    @Test
    void getPaymentById_Success() throws PaymentNotFoundException {
        Payment mockPayment = new Payment();
        mockPayment.setPaymentId("PAY-123");
        mockPayment.setPayerName("Test User");

        when(repository.findByPaymentId("PAY-123")).thenReturn(Optional.of(mockPayment));

        PaymentResponseDto response = paymentService.getPaymentById("PAY-123");

        assertEquals("PAY-123", response.getPaymentId());
        assertEquals("Test User", response.getPayerName());
    }

    @Test
    void getPaymentByName_ThrowsException_WhenListEmpty() {
        when(repository.findByPayerName("Unknown")).thenReturn(Collections.emptyList());

        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.getPaymentByName("Unknown");
        });
    }
}
