package com.project.paymentservice.util;

import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.exception.InvalidPaymentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.AssertionsKt.assertNull;

public class PaymentUtilTest {
    @Test
    void testGeneratePaymentId() {
        String paymentId = PaymentUtil.generatePaymentId();
        assertTrue(paymentId.startsWith("PAY-"));
        assertEquals(12, paymentId.length());
    }

    @Test
    void testMaskUpiId() {
        assertEquals("jo****@okaxis", PaymentUtil.maskUpiId("john.doe@okaxis"));
        assertEquals("a****@ybl", PaymentUtil.maskUpiId("a@ybl"));
        assertEquals("invalidUpi", PaymentUtil.maskUpiId("invalidUpi")); // No @ symbol
        assertNull(PaymentUtil.maskUpiId(null));
    }

    @Test
    void testValidatePaymentRequest_Valid() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setPayerName("John Doe");
        request.setUpiId("john@ybl");
        request.setAmount(new BigDecimal("100.00"));

        assertDoesNotThrow(() -> PaymentUtil.validatePaymentRequest(request));
    }

    @Test
    void testValidatePaymentRequest_InvalidAmount() {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setPayerName("John Doe");
        request.setUpiId("john@ybl");
        request.setAmount(BigDecimal.ZERO);

        InvalidPaymentException exception = assertThrows(InvalidPaymentException.class,
                () -> PaymentUtil.validatePaymentRequest(request));
        assertEquals("Payment amount must be greater than zero", exception.getMessage());
    }
}
