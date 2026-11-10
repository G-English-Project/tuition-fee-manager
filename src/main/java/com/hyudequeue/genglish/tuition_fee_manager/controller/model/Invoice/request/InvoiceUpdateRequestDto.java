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
    private Integer year;
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
        if (year != null) invoice.setYear(year);
        if (dueDate != null) invoice.setDueDate(dueDate);
        if (classes != null) invoice.setClasses(classes);

        // If year was not explicitly provided but both month and dueDate are available,
        // infer billing year using the standard rule:
        // if MONTH(dueDate) < billingMonth → billingYear = YEAR(dueDate) - 1
        if (year == null && invoice.getMonth() != null && invoice.getDueDate() != null) {
            int dueDateMonth = invoice.getDueDate().getMonthValue();
            int dueDateYear = invoice.getDueDate().getYear();
            int billingYear = (dueDateMonth < invoice.getMonth()) ? dueDateYear - 1 : dueDateYear;
            invoice.setYear(billingYear);
        }

        invoice.setTotalAmount(calculateTotalAmount());
        invoice.setUpdatedAt(LocalDateTime.now());
    }
}

