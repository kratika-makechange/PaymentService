package com.project.paymentservice.controller;

import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseCreatedDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.entity.Payment;
import com.project.paymentservice.exception.InvalidPaymentException;
import com.project.paymentservice.exception.InvalidPaymentStatusException;
import com.project.paymentservice.exception.PaymentNotFoundException;
import com.project.paymentservice.service.PaymentService;
import com.project.paymentservice.util.PaymentUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
    @RequestMapping("/v1/payments")
    public class PaymentController {

        private final PaymentService paymentService;


        // Constructor injection
        public PaymentController(PaymentService paymentService ) {
            this.paymentService = paymentService;
        }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PaymentRequestDto request) throws InvalidPaymentException {

        // 1. Get the complete response object from the service
        PaymentResponseDto response = paymentService.createPayment(idempotencyKey, request);

        // 2. Pass the entire object and the HTTP status (201 CREATED)
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

        @GetMapping("/getPayments/{paymentId}")
        ResponseEntity<PaymentResponseDto> findByPaymentId(@PathVariable String paymentId) throws PaymentNotFoundException {
            PaymentResponseDto response=paymentService.getPaymentById(paymentId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    @GetMapping("/getPayments/name/{payerName}")
   public ResponseEntity<List<PaymentResponseDto>> findByPayerName(@PathVariable("payerName") String payerName) throws PaymentNotFoundException {
        List<PaymentResponseDto> response=paymentService.getPaymentByName(payerName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{paymentId}/status")
    public ResponseEntity<PaymentResponseDto> updateStatus(@PathVariable String paymentId, PaymentStatus status) throws InvalidPaymentStatusException, InvalidPaymentStatusException, PaymentNotFoundException {

            PaymentResponseDto response=paymentService.updatePaymentStatus(paymentId, status);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/ping")
    public String ping() {
        return "Controller is working!";
    }
    }

