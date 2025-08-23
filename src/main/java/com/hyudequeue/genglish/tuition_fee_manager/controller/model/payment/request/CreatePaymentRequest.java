package com.hyudequeue.genglish.tuition_fee_manager.controller.model.payment.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull
    private Long invoiceId;

    @NotBlank
    private String buyerName;

    @Email
    @NotBlank
    private String buyerEmail;

    @NotBlank
    private String buyerPhone;

    @Size(max = 24, message = "Description must not exceed 24 characters")
    private String description;

    @Positive(message = "expiredAt (seconds) must be a positive number")
    private Long expiredAt;
}
