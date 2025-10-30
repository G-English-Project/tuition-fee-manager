package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.CategoryStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClassCategoryResponseDto {
    private Long categoryId;
    private String color;
    private String name;
    private CategoryStatusEnum status;

    public static ClassCategoryResponseDto fromEntity(ClassCategory entity) {
        return ClassCategoryResponseDto.builder()
                .categoryId(entity.getCategoryId())
                .color(entity.getColor())
                .name(entity.getName())
                .status(entity.getStatus())
                .build();
    }
}