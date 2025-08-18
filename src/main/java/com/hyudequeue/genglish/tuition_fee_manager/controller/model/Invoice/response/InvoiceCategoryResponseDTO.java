package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.InvoiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceCategoryResponseDTO {
    private Long categoryId;
    private String name;
    private String color;

    public static InvoiceCategoryResponseDTO toDto(InvoiceCategory category) {
        return InvoiceCategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .color(category.getColor())
                .build();
    }
}
