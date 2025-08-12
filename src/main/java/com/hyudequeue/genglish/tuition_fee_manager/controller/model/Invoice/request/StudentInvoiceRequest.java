package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentInvoiceRequest {
    @NotNull
    private Long userId;

    @NotNull
    private List<InvoiceItemRequestDTO> items;
}
