package com.project.paymentservice.controller;

import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseCreatedDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PaymentController.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createPayment_Returns201Created() throws Exception {
        PaymentRequestDto request = new PaymentRequestDto();
        request.setPayerName("Test User");
        request.setUpiId("test@upi");
        request.setAmount(new java.math.BigDecimal("1000.00"));

        PaymentResponseCreatedDto responseDto = new PaymentResponseCreatedDto("PAY-123", PaymentStatus.SUCCESS, "Success");

        when(paymentService.createPayment(any(PaymentRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value("PAY-123"));
    }

    @Test
    void findByPayerName_Returns200Ok_WithList() throws Exception {
        PaymentResponseDto responseDto = new PaymentResponseDto("PAY-123", "Test User", "test@upi", new java.math.BigDecimal("1000.00"), PaymentStatus.SUCCESS, "Success");

        when(paymentService.getPaymentByName("Test User")).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/v1/payments/getPayments/name/Test User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value("PAY-123"))
                .andExpect(jsonPath("$[0].payerName").value("Test User"));
    }
}