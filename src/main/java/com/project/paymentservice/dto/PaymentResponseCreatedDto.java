package com.project.paymentservice.dto;

import com.project.paymentservice.Enum.PaymentStatus;

public class PaymentResponseCreatedDto {

    private String paymentId;
    private PaymentStatus status;
    private String message;

    public PaymentResponseCreatedDto(String paymentId, PaymentStatus status, String message) {
        this.paymentId = paymentId;
        this.status = status;
        this.message = message;
    }

    public String getPaymentId() {
        return paymentId;
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
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
