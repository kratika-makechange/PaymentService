package com.project.paymentservice.controller;

import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.exception.InvalidPaymentException;
import com.project.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/v1/payments")
    public class PaymentController {

        private final PaymentService paymentService;

        // Constructor injection
        public PaymentController(PaymentService paymentService) {
            this.paymentService = paymentService;
        }

        @PostMapping
        public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto request) throws InvalidPaymentException {
            PaymentResponseDto response = paymentService.createPayment(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }

