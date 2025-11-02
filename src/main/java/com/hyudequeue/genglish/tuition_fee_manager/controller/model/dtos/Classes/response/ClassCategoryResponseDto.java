package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassCategoryResponseDto {
    private Long categoryId;
    private String name;
    private String color;

    public static ClassCategoryResponseDto fromEntity(ClassCategory category) {
        return ClassCategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .color(category.getColor())
                .build();
    }
}