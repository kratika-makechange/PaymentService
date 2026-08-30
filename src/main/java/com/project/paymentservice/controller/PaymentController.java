package com.project.paymentservice.controller;

import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseCreatedDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.entity.Payment;
import com.project.paymentservice.exception.InvalidPaymentException;
import com.project.paymentservice.exception.PaymentNotFoundException;
import com.project.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
    @RequestMapping("/v1/payments")
    public class PaymentController {

        private final PaymentService paymentService;

        // Constructor injection
        public PaymentController(PaymentService paymentService) {
            this.paymentService = paymentService;
        }

        @PostMapping
        public ResponseEntity<PaymentResponseCreatedDto> createPayment(@Valid @RequestBody PaymentRequestDto request) throws InvalidPaymentException {
            PaymentResponseCreatedDto response = paymentService.createPayment(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @GetMapping("/getPayments/{paymentId}")
        ResponseEntity<PaymentResponseDto> findByPaymentId(@PathVariable String paymentId) throws PaymentNotFoundException {
            PaymentResponseDto response=paymentService.getPaymentById(paymentId);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

    @GetMapping("/getPayments/name/{payerName}")
    ResponseEntity<PaymentResponseDto> findByPayerName(@PathVariable String payerName) throws PaymentNotFoundException {
        PaymentResponseDto response=paymentService.getPaymentByName(payerName);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
    }

