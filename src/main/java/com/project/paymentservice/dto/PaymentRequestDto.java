package com.project.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PaymentRequestDto {

        @NotBlank(message = "Payer name cannot be empty")
        private String payerName;

        @NotBlank(message = "Invalid UPI ID")
        @Pattern(regexp = "^[a-zA-Z0-9.\\-_]+@[a-zA-Z]+$", message = "Invalid UPI ID")
        private String upiId;

        @NotNull(message = "Payment amount is required")
        @Positive(message =
                "Payment amount must be greater than zero")
        private BigDecimal amount;

        // Constructors
        public PaymentRequestDto() {}

        public PaymentRequestDto(String payerName, String upiId, BigDecimal amount) {
            this.payerName = payerName;
            this.upiId = upiId;
            this.amount = amount;
        }

        // Getters and Setters
        public String getPayerName() { return payerName; }
        public void setPayerName(String payerName) { this.payerName = payerName; }

        public String getUpiId() { return upiId; }
        public void setUpiId(String upiId) { this.upiId = upiId; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

}
