package com.project.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.paymentservice.Enum.PaymentStatus;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponseDto {
        private String paymentId;
        private String payerName;
        private String upiId;
        private BigDecimal amount;
        private PaymentStatus status;
        private String message;

        public PaymentResponseDto(String paymentId, String payerName, String upiId, BigDecimal amount,PaymentStatus status, String message) {
            this.paymentId = paymentId;
            this.payerName=payerName;
            this.upiId=upiId;
            this.amount=amount;
            this.status = status;
            this.message = message;
        }

        public String getPaymentId() {
            return paymentId;
        }

    public String getPayerName() {
        return payerName;
    }

    public String getUpiId() {
        return upiId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "PaymentResponseDto{" +
                    "paymentId='" + paymentId + '\'' +
                    "payerName="  + payerName + '\'' +
                    "upiId="  + upiId + '\'' +
                    "amount=" + amount + '\'' +
                    ", status='" + status + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
