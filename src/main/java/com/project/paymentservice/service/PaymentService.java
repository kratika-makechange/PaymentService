package com.project.paymentservice.service;


import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseCreatedDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.entity.Payment;
import com.project.paymentservice.exception.InvalidPaymentException;
import com.project.paymentservice.exception.PaymentNotFoundException;
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
        private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }


    @Transactional(rollbackFor = InvalidPaymentException.class)
    public PaymentResponseCreatedDto createPayment(PaymentRequestDto request) throws InvalidPaymentException {
            logger.info("Payment Started");


            String paymentId = PaymentUtil.generatePaymentId();
            Payment payment=new Payment();
            payment.setPayerName(request.getPayerName());
            payment.setPaymentId(paymentId);
            payment.setUpiId(request.getUpiId());
            payment.setAmount(request.getAmount());
            payment.setCreatedAt(LocalDateTime.now());

                PaymentUtil.validatePaymentRequest(request);
                payment.setStatus(PaymentStatus.SUCCESS);
                logger.info("Payment Successful for ID: {}", paymentId);
                repository.save(payment);

                return new PaymentResponseCreatedDto(
                        paymentId, payment.getStatus(),
                        "Payment completed successfully"
                );


        }

        public PaymentResponseDto getPaymentById(String id)throws PaymentNotFoundException{

            Payment payment=repository.findByPaymentId(id).
                orElseThrow(()-> new PaymentNotFoundException("Payment not found with id :"+ id));

            return new PaymentResponseDto(
                    payment.getPaymentId(),
                    payment.getPayerName(),
                    payment.getUpiId(),
                    payment.getAmount(),
                    payment.getStatus(),
                    "Payment retrieved successfully"
            );

        }

    public List<PaymentResponseDto> getPaymentByName(String payerName)throws PaymentNotFoundException{

        List<Payment> payments = repository.findByPayerName(payerName);
        if (payments.isEmpty()) {
            throw new PaymentNotFoundException("Payment not found for name :" + payerName);
        }


        return payments.stream()
                .map(payment -> new PaymentResponseDto(
                payment.getPaymentId(),
                payment.getPayerName(),
                payment.getUpiId(),
                payment.getAmount(),
                payment.getStatus(),
                "Payment retrieved successfully"
        )).toList();

    }
    }
