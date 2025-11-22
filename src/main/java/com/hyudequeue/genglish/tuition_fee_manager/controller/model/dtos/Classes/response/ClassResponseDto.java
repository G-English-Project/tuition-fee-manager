package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.GenerateId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassResponseDto {
    private Long classId;
    private String shownId;
    private String className;
    private String description;
    private ClassStatusEnum status;
    private BigDecimal amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CategoryDto> categories;

    private List<UserResponseDto> mentors;

    private Integer currentStudentCount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDto {
        private Long categoryId;
        private String name;
        private String color;
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

                null, // categories sẽ set sau
                null, // mentors sẽ set sau
                null  // currentStudentCount set sau
        );

        // ✅ Set categories
        if (c.getCategories() != null) {
            dto.setCategories(
                    c.getCategories().stream()
                            .map(cat -> new CategoryDto(
                                    cat.getCategoryId(),
                                    cat.getName(),
                                    cat.getColor()
                            ))
                            .toList()
            );
        }

        // ✅ Set mentors (nhiều mentorBy)
        if (c.getMentors() != null) {
            dto.setMentors(
                    c.getMentors().stream()
                            .map(UserResponseDto::toDto) // dùng DTO có sẵn của bạn
                            .toList()
            );
        }

        return dto;
    }
}



