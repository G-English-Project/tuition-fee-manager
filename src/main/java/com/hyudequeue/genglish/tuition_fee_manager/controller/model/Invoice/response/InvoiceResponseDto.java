package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.GenerateId;
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
    private String shownId; // thêm field này
    private Long userId;
    private String userName;
    private Long classesId;
    private Integer month;
    private LocalDate dueDate;
    private InvoiceStatusEnum status;
    private Integer totalAmount;
    private List<InvoiceItemResponseDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private PaymentMethodEnum paymentMethod;

    private List<InvoiceCategoryResponseDTO> categories;

    public static InvoiceResponseDto toDto(Invoice invoice) {
        return InvoiceResponseDto.builder()
                .invoiceId(invoice.getInvoiceId())
                .shownId(GenerateId.formatId(invoice.getInvoiceId())) // gán shownId
                .userId(invoice.getUser().getUserId())
                .userName(invoice.getUserName() != null ? invoice.getUserName() : invoice.getUser().getFullName())
                .classesId(invoice.getClasses().getClassId())
                .month(invoice.getMonth())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .totalAmount(invoice.getTotalAmount())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .paidAt(invoice.getPaidAt())
                .items(invoice.getItems().stream()
                        .map(InvoiceItemResponseDTO::toDto)
                        .toList())
                .paymentMethod(invoice.getPaymentType())
                .categories(invoice.getCategories().stream()
                        .map(InvoiceCategoryResponseDTO::toDto)
                        .toList())
                .build();
    }
}
