package com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import vn.payos.type.PaymentData;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull
    private Long invoiceId;
    @NotBlank private String buyerName;

    @Email
    @NotBlank private String buyerEmail;

    @NotBlank private String buyerPhone;
}