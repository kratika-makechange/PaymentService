package com.project.paymentservice.exception;

public class InvalidPaymentStatusException extends Exception{
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
}
