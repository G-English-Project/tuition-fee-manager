package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDto {
    private Long invoiceId;
    private Long userId;
    private Long classesId;
    private Integer month;
    private LocalDate dueDate;
    private InvoiceStatusEnum status;
    private Integer totalAmount;
    private List<InvoiceItemResponseDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InvoiceResponseDto toDto(Invoice invoice) {
        return InvoiceResponseDto.builder()
                .invoiceId(invoice.getInvoiceId())
                .userId(invoice.getUser().getUserId())
                .classesId(invoice.getClasses().getClassId())
                .month(invoice.getMonth())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .totalAmount(invoice.getTotalAmount())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .items(invoice.getItems().stream().map(InvoiceItemResponseDTO::toDto).toList())
                .build();
    }

}
