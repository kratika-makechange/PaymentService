package com.project.paymentservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        // Handles custom business logic exceptions
        @ExceptionHandler(InvalidPaymentException.class)
        public ResponseEntity<Map<String, String>> handleInvalidPaymentException(InvalidPaymentException ex) {
            logger.error("Business Validation Failed: {}", ex.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Payment processing failed");
            errorResponse.put("message", ex.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(PaymentNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleInvalidPaymentException(PaymentNotFoundException ex){

            Map<String, String> error=new HashMap<>();
            error.put("error", "payment not found");
            error.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(error);
        }

        // Handles @Valid annotation failures from the Controller
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();

            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );

            logger.error("Request Validation Failed: {}", errors);

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        // Fallback for any other unexpected exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
            logger.error("An unexpected error occurred: ", ex);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "An unexpected error occurred. Please try again later.");

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(InvalidPaymentStatusException.class)
        public ResponseEntity<Map<String,String>> handleInvalidPaymentStatusException(Exception ex){
            Map<String, String> error=new HashMap<>();
            error.put("error", "Given status is not valid");
            error.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(error);
        }
    }
