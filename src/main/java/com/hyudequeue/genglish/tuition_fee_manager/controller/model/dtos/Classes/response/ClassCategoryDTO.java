package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassCategoryDTO {
    private Long categoryId;
    private String name;
    private String color;
    private String code;

    public static ClassCategoryDTO fromEntity(ClassCategory category) {
        if (category == null) return null;
        return new ClassCategoryDTO(
                category.getCategoryId(),
                category.getName(),
                category.getColor(),
                category.getCode()
        );
    }
}
