package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.InvoiceItem;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemResponseDTO {
    private Long itemId;
    private String feeName;
    private String description;
    private Integer amount;
    private Integer quantity;

    public static InvoiceItemResponseDTO toDto(InvoiceItem invoiceItem){
        return InvoiceItemResponseDTO.builder()
                .itemId(invoiceItem.getItemId())
                .feeName(invoiceItem.getFeeName())
                .description(invoiceItem.getDescription())
                .amount(invoiceItem.getAmount())
                .quantity(invoiceItem.getQuantity())
                .build();
    }
}
