package com.project.paymentservice.service;


import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.exception.InvalidPaymentException;
import com.project.paymentservice.util.PaymentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
    public class PaymentService {

        private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
        private static final String STATUS_SUCCESS = "SUCCESS";

        public PaymentResponseDto createPayment(PaymentRequestDto request) throws InvalidPaymentException {
            logger.info("Payment Started");

            try {
                PaymentUtil.validatePaymentRequest(request);
            } catch (InvalidPaymentException e) {
                logger.error("Validation Failed: {}", e.getMessage());
                throw e;
            }

            String paymentId = PaymentUtil.generatePaymentId();

            logger.info("Payment Successful for ID: {}", paymentId);

            return new PaymentResponseDto(
                    paymentId,
                    STATUS_SUCCESS,
                    "Payment completed successfully"
            );
        }
    }
