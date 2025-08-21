package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.InvoiceItem;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceCreateRequestDTO {
    private Long userId;
    private Long classId;
    private LocalDate dueDate;
    private Integer month;
    private List<InvoiceItemRequestDTO> items;

    public Invoice toEntity() {
        return Invoice.builder()
                .dueDate(dueDate)
                .month(month)
                .status(InvoiceStatusEnum.UNPAID)
                .totalAmount(calculateTotalAmount(items))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Integer calculateTotalAmount(List<InvoiceItemRequestDTO> items) {
        if (items == null) return 0;
        return items.stream()
                .mapToInt(item -> item.getAmount() * item.getQuantity())
                .sum();
    }

    public List<InvoiceItem> toInvoiceItems(Invoice invoice) {
        if (items == null) return List.of();
        return items.stream()
                .map(dto -> dto.toEntity(invoice))
                .collect(Collectors.toList());
    }
}
