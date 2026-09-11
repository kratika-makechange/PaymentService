package com.project.paymentservice.mapper;

import com.project.paymentservice.Enum.PaymentStatus;
import com.project.paymentservice.dto.PaymentRequestDto;
import com.project.paymentservice.dto.PaymentResponseCreatedDto;
import com.project.paymentservice.dto.PaymentResponseDto;
import com.project.paymentservice.entity.Payment;
import com.project.paymentservice.util.PaymentUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {PaymentUtil.class, PaymentStatus.class, LocalDateTime.class})
public interface PaymentMapper {

    // --- 1. Request to Entity Mapping ---

    // Map the extra method parameter to the entity field
    @Mapping(target = "idempotencyKey", source = "idempotencyKey")

    // Use Java expressions to generate dynamic fields
    @Mapping(target = "paymentId", expression = "java(PaymentUtil.generatePaymentId())")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")

    // Set constants
    @Mapping(target = "status", constant = "PENDING")

    // Ignore the database auto-generated primary key
    @Mapping(target = "id", ignore = true)
    Payment toEntity(PaymentRequestDto request, String idempotencyKey);


    // --- 2. Entity to Response Mapping ---

    @Mapping(target = "message", source = "message")
    @Mapping(target = "upiId", expression = "java(PaymentUtil.maskUpiId(payment.getUpiId()))")
    PaymentResponseDto toDto(Payment payment, String message);

     PaymentResponseDto toPostResponseDto(Payment payment, String message);

}
