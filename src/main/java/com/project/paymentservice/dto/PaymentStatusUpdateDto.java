package com.project.paymentservice.dto;

import com.project.paymentservice.Enum.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class PaymentStatusUpdateDto {

    @NotNull
    private PaymentStatus status;

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
