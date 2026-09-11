package com.project.paymentservice.service;


import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseCreatedDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.entity.Payment;
import com.project.paymentservice.exception.InvalidPaymentException;
import com.project.paymentservice.exception.InvalidPaymentStatusException;
import com.project.paymentservice.exception.PaymentNotFoundException;
import com.project.paymentservice.mapper.PaymentMapper;
import com.project.paymentservice.repository.PaymentRepository;
import com.project.paymentservice.util.PaymentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
    public class PaymentService {
        private final PaymentRepository repository;
        private final PaymentProcessor process;
        private final PaymentMapper paymentMapper;
        private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository repository, PaymentProcessor process, PaymentMapper paymentMapper) {
        this.repository = repository;
        this.process = process;
        this.paymentMapper = paymentMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponseDto createPayment(String idempotencyKey, PaymentRequestDto request) throws InvalidPaymentException {
        logger.info("Payment Started with Idempotency Key: {}", idempotencyKey);

        // 1. Check Idempotency FIRST
        Optional<Payment> existingPayment = repository.findByIdempotencyKey(idempotencyKey);
        if (existingPayment.isPresent()) {
            logger.info("Idempotency Hit! Returning same payment details for key: {}", idempotencyKey);
            return paymentMapper.toPostResponseDto(existingPayment.get(), "Payment previously processed");
        }

        // 2. Validate SECOND
        PaymentUtil.validatePaymentRequest(request);

        // 3. Use Mapper for entity creation
        Payment payment = paymentMapper.toEntity(request, idempotencyKey);
        repository.save(payment);
        logger.info("Payment saved with status PENDING for ID: {}", payment.getPaymentId());

        // 4. Process
        PaymentStatus finalStatus = process.processPayment(payment);
        payment.setStatus(finalStatus);

        repository.save(payment);
        logger.info("Payment finished with status: {} for ID: {}", finalStatus, payment.getPaymentId());

        String message = finalStatus == PaymentStatus.SUCCESS
                ? "Payment completed successfully"
                : "Payment processing failed";

        // 5. Use Mapper for response
        return paymentMapper.toPostResponseDto(payment, message);
    }

    public PaymentResponseDto getPaymentById(String id) throws PaymentNotFoundException {
        Payment payment = repository.findByPaymentId(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));

        // Use mapper to ensure consistent DTO structure and UPI masking
        return paymentMapper.toDto(payment, "Payment retrieved successfully");
    }

    public List<PaymentResponseDto> getPaymentByName(String payerName) throws PaymentNotFoundException {
        List<Payment> payments = repository.findByPayerName(payerName);
        if (payments.isEmpty()) {
            throw new PaymentNotFoundException("Payment not found for name: " + payerName);
        }

        // Stream through and map each entity using the mapper
        return payments.stream()
                .map(payment -> paymentMapper.toDto(payment, "Payment retrieved successfully"))
                .toList();
    }

    public PaymentResponseDto updatePaymentStatus(String paymentId, PaymentStatus status) throws InvalidPaymentStatusException, PaymentNotFoundException {
        Payment payment = repository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus().equals(PaymentStatus.PENDING)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            repository.save(payment);

            // Use mapper here as well
            return paymentMapper.toDto(payment, "Payment updated successfully");
        }

        // Fixed exception string concatenation
        throw new InvalidPaymentStatusException("Payment already completed for paymentId " + paymentId + " with status " + payment.getStatus());
    }
    }
