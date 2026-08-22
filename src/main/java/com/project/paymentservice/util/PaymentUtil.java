package com.project.paymentservice.util;

import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.exception.InvalidPaymentException;
import java.util.UUID;

    public class PaymentUtil {

        private static final String PAYMENT_PREFIX = "PAY-";
        // Basic regex pattern to validate UPI ID format (e.g., name@bank)
        private static final String UPI_PATTERN = "^[a-zA-Z0-9.\\-_]+@[a-zA-Z]+$";

        public static String generatePaymentId() {
            return PAYMENT_PREFIX + UUID.randomUUID().toString().substring(0, 8);
        }

        public static void validatePaymentRequest(PaymentRequestDto request) throws InvalidPaymentException {
            if (request == null) {
                throw new InvalidPaymentException("Payment request cannot be null");
            }

            if (request.getPayerName() == null || request.getPayerName().isBlank()) {
                throw new InvalidPaymentException("Payer name cannot be empty");
            }

            if (request.getUpiId() == null || request.getUpiId().isBlank() || !request.getUpiId().matches(UPI_PATTERN)) {
                throw new InvalidPaymentException("Invalid UPI ID");
            }

            if (request.getAmount() <= 0) {
                throw new InvalidPaymentException("Payment amount must be greater than zero");
            }
        }
    }

