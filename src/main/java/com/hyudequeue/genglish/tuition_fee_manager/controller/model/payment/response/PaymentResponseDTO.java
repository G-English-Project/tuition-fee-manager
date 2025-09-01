package com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long paymentId;
    private String shownId;
    private int amount;
    private String currency;
    private String description;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    String cancelUrl;
    String returnUrl;
    private LocalDateTime createdAt;
    private PaymentStatusEnum status;

    public static PaymentResponseDTO toResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .shownId(payment.getShownId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .description(payment.getDescription())
                .buyerName(payment.getBuyerName())
                .buyerEmail(payment.getBuyerEmail())
                .buyerPhone(payment.getBuyerPhone())
                .cancelUrl(payment.getCancelUrl())
                .returnUrl(payment.getReturnUrl())
                .createdAt(payment.getCreatedAt())
                .status(payment.getStatus())
                .build();
    }
}
