package com.project.paymentservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.dto.PaymentStatusUpdateDto;
import com.project.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@WebMvcTest(PaymentController.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPayment_ReturnsCreated() throws Exception {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setPayerName("John");
        request.setUpiId("john@okaxis");
        request.setAmount(new BigDecimal("100.00"));

        when(paymentService.createPayment(any(), any())).thenReturn(new PaymentResponseDto(null, null, null, null, null, null));

        mockMvc.perform(post("/v1/payments")
                        .header("Idempotency-Key", "test-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void findByPaymentId_ReturnsOk() throws Exception {
        when(paymentService.getPaymentById("PAY-123")).thenReturn(new PaymentResponseDto(null, null, null, null, null, null));

        mockMvc.perform(get("/v1/payments/getPayments/PAY-123"))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_ReturnsOk() throws Exception {
        PaymentStatusUpdateDto updateDto = new PaymentStatusUpdateDto();
        updateDto.setStatus(PaymentStatus.SUCCESS);

        when(paymentService.updatePaymentStatus(eq("PAY-123"), any())).thenReturn(new PaymentResponseDto(null, null, null, null, null, null));

        mockMvc.perform(patch("/v1/payments/PAY-123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }
}