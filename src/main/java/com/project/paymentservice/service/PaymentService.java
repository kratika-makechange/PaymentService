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

import java.time.LocalDateTime;
import java.util.Optional;


@Service
    public class PaymentService {
        private final PaymentRepository repository;
        private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public PaymentResponseCreatedDto createPayment(PaymentRequestDto request) throws InvalidPaymentException {
            logger.info("Payment Started");


            String paymentId = PaymentUtil.generatePaymentId();
            Payment payment=new Payment();
            payment.setPayerName(request.getPayerName());
            payment.setPayerId(paymentId);
            payment.setUpiId(request.getUpiId());
            payment.setAmount(request.getAmount());
            payment.setCreatedAt(LocalDateTime.now());

            try {
                PaymentUtil.validatePaymentRequest(request);
                payment.setStatus(PaymentStatus.SUCCESS);
                logger.info("Payment Successful for ID: {}", paymentId);
                repository.save(payment);

                return new PaymentResponseCreatedDto(
                        paymentId, payment.getStatus(),
                        "Payment completed successfully"
                );

            }catch(Exception e){
                logger.info("Payment Failed for ID: {} - Reason: {}", paymentId, e.getMessage());
                payment.setStatus(PaymentStatus.FAILED);
                repository.save(payment);

                if(e instanceof InvalidPaymentException){
                    throw (InvalidPaymentException) e;
                }

                throw new InvalidPaymentException(e.getMessage());
            }
        }

        public PaymentResponseDto getPaymentById(String id)throws PaymentNotFoundException{

            Payment payment=repository.findByPayerId(id).
                orElseThrow(()-> new PaymentNotFoundException("Payment not found with id :"+ id));

            return new PaymentResponseDto(
                    payment.getPayerId(),
                    payment.getPayerName(),
                    payment.getUpiId(),
                    payment.getAmount(),
                    payment.getStatus(),
                    "Payment retrieved successfully"
            );

        }

    public PaymentResponseDto getPaymentByName(String payerName)throws PaymentNotFoundException{

        Payment payment=repository.findByPayerName(payerName).
                orElseThrow(()-> new PaymentNotFoundException("Payment not found for name :"+ payerName));

        return new PaymentResponseDto(
                payment.getPayerId(),
                payment.getPayerName(),
                payment.getUpiId(),
                payment.getAmount(),
                payment.getStatus(),
                "Payment retrieved successfully"
        );

    }
    }
