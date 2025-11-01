package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.GenerateId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassResponseDto {
    private Long classId;
    private String shownId;
    private String className;
    private String description;
    private ClassStatusEnum status;
    private Integer amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Thêm danh sách category trả về
    private java.util.List<CategoryDto> categories;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDto {
        private Long categoryId;
        private String name;
    }

    public static ClassResponseDto fromEntity(Classes c) {
        if (c == null) return null;

        ClassResponseDto dto = new ClassResponseDto(
                c.getClassId(),
                GenerateId.formatId(c.getClassId()),
                c.getClassName(),
                c.getDescription(),
                c.getStatus(),
                c.getAmount(),
                c.getEffectiveFrom(),
                c.getEffectiveTo(),
                c.getCreatedAt(),
                c.getUpdatedAt(),
                null // Tạm để tí set bên dưới
        );

        // ✅ Map category sang DTO
        if (c.getCategories() != null) {
            dto.setCategories(
                    c.getCategories().stream()
                            .map(cat -> new CategoryDto(
                                    cat.getCategoryId(),
                                    cat.getName()
                            ))
                            .toList()
            );
        }

        return dto;
    }
}

