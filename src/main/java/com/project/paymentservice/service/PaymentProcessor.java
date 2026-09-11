package com.project.paymentservice.service;

import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.entity.Payment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentProcessor {

    private static final BigDecimal MAX_TRASACTION_LIMIT= new BigDecimal("1000000.00");
    public PaymentStatus processPayment(Payment payment){

       if(payment.getAmount()==null){
           return PaymentStatus.FAILED;
       }

       if(payment.getAmount().compareTo(MAX_TRASACTION_LIMIT)>0){
           return PaymentStatus.FAILED;
       }
        return PaymentStatus.SUCCESS;
    }
}
