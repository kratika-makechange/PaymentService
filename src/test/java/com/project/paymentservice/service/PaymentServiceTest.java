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

import java.math.BigDecimal;
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
        PaymentResponseDto expectedResponse = new PaymentResponseDto(null,null,null,null,null,null);

        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));
        when(paymentMapper.toPostResponseDto(eq(existingPayment), anyString())).thenReturn(expectedResponse);

        PaymentResponseDto result = paymentService.createPayment(idempotencyKey, new PaymentRequestDto());

        assertEquals(expectedResponse, result);

        // Verifying the database isn't hit for a save
        verify(repository, never()).save(any());

        // NEW: Verifying the processor is never invoked for a duplicate key
        verify(processor, never()).processPayment(any());
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

    @Test
    void createPayment_NewRequest_ProcessesAndReturnsSuccess() throws Exception {
        // 1. Setup Data
        String idempotencyKey = "key-123";

        PaymentRequestDto request = new PaymentRequestDto();
        request.setPayerName("John");
        request.setUpiId("john@upi");
        request.setAmount(new BigDecimal("500.00"));

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING); // Initial status from mapper

        PaymentResponseDto expectedResponse = new PaymentResponseDto(null, null,null,null,null,null);
        // (Add setters here if you want to validate specific response fields)

        // 2. Mocking the dependencies
        // No existing payment found (simulating a brand new request)
        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());

        // Mock the mapper to return our pending entity
        when(paymentMapper.toEntity(request, idempotencyKey)).thenReturn(payment);

        // Mock the processor to return SUCCESS
        when(processor.processPayment(payment)).thenReturn(PaymentStatus.SUCCESS);

        // Mock the final response mapping
        when(paymentMapper.toPostResponseDto(eq(payment), anyString())).thenReturn(expectedResponse);

        // 3. Execute the method being tested
        PaymentResponseDto result = paymentService.createPayment(idempotencyKey, request);

        // 4. Assertions and Verifications
        assertEquals(expectedResponse, result, "The response DTO should match the expected output");
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus(), "The payment status should be updated to SUCCESS");

        // Verify that save() was called exactly twice (once for PENDING, once for SUCCESS)
        verify(repository, times(2)).save(payment);

        // Verify the processor was actually called
        verify(processor).processPayment(payment);
    }


}
