package com.project.paymentservice.service;


import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.entity.Payment;
import com.project.paymentservice.exception.InvalidPaymentStatusException;
import com.project.paymentservice.mapper.PaymentMapper;
import com.project.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository repository;
    @Mock
    private PaymentProcessor processor;
    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_IdempotencyHit_ReturnsExisting() throws Exception {
        String idempotencyKey = "key-123";
        Payment existingPayment = new Payment();
        PaymentResponseDto expectedResponse = new PaymentResponseDto(null, null, null, null, null, null);

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));
        when(paymentMapper.toPostResponseDto(eq(existingPayment), anyString())).thenReturn(expectedResponse);

        PaymentResponseDto result = paymentService.createPayment(idempotencyKey, new PaymentRequestDto());

        assertEquals(expectedResponse, result);
        verify(repository, never()).save(any());
    }

    @Test
    void updatePaymentStatus_ValidTransition_UpdatesAndReturns() throws Exception {
        String paymentId = "PAY-123";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        PaymentResponseDto expectedResponse = new PaymentResponseDto(null, null, null, null, null, null);

        when(repository.findByPaymentId(paymentId)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(eq(payment), anyString())).thenReturn(expectedResponse);

        PaymentResponseDto result = paymentService.updatePaymentStatus(paymentId, PaymentStatus.SUCCESS);

        assertEquals(expectedResponse, result);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        verify(repository).save(payment);
    }

    @Test
    void updatePaymentStatus_AlreadyCompleted_ThrowsException() {
        String paymentId = "PAY-123";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.SUCCESS);

        when(repository.findByPaymentId(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(InvalidPaymentStatusException.class,
                () -> paymentService.updatePaymentStatus(paymentId, PaymentStatus.FAILED));
    }
}
