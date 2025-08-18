package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInvoiceRequest {

    @NotNull
    private Long userId;

    @NotNull
    @Size(min = 1, message = "Items must not be empty")
    private List<InvoiceItemRequestDTO> items;

    @Builder.Default
    private List<Long> categoryIds = new ArrayList<>();

    private PaymentMethodEnum paymentType;
}
