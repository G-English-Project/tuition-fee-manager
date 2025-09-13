package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.InvoiceItem;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceUpdateRequestDto {
    private List<InvoiceItemRequestDTO> updatedItems;
    private List<Long> categoryIds;
    private Integer month;
    private LocalDate dueDate;
    private Long classId;   // 👈 only the ID, not the whole entity

    public Integer calculateTotalAmount() {
        if (updatedItems == null) return 0;
        return updatedItems.stream()
                .mapToInt(i -> i.getAmount() * i.getQuantity())
                .sum();
    }

    public List<InvoiceItem> toInvoiceItems(Invoice invoice) {
        if (updatedItems == null) return List.of();
        return updatedItems.stream()
                .map(item -> item.toEntity(invoice))
                .collect(Collectors.toList());
    }

    public void applyTo(Invoice invoice, Classes classes) {
        if (month != null) invoice.setMonth(month);
        if (dueDate != null) invoice.setDueDate(dueDate);
        if (classes != null) invoice.setClasses(classes);

        invoice.setTotalAmount(calculateTotalAmount());
        invoice.setUpdatedAt(LocalDateTime.now());
    }
}

