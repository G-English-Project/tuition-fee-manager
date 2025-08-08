package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.InvoiceItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemRequestDTO {
    private Long itemId;
    private String feeName;
    private String description;
    private Integer amount;
    private Integer quantity;

    public static InvoiceItemRequestDTO toDto(InvoiceItem invoiceItem){
        return InvoiceItemRequestDTO.builder()
                .itemId(invoiceItem.getItemId())
                .feeName(invoiceItem.getFeeName())
                .description(invoiceItem.getDescription())
                .amount(invoiceItem.getAmount())
                .quantity(invoiceItem.getQuantity())
                .build();
    }

    public InvoiceItem toEntity(Invoice invoice) {
        return InvoiceItem.builder()
                .itemId(itemId)
                .invoice(invoice)
                .feeName(feeName)
                .description(description)
                .amount(amount)
                .quantity(quantity)
                .build();
    }
}
