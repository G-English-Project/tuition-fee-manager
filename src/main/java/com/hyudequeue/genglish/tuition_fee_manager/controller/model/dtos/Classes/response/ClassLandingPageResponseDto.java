package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ClassLandingPageResponseDto {
    private Long classId;
    private String className;
    private String description;
    private BigDecimal amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    // ⭐ LIST mentors
    private List<UserResponseDto> mentors;

    private Integer currentStudentCount;
    private List<CategoryTagDto> categories;

    @Data
    @Builder
    public static class CategoryTagDto {
        private Long categoryId;
        private String name;
        private String color;
    }

    public static ClassLandingPageResponseDto fromEntity(Classes classes, Integer studentCount) {

        // ⭐ Map categories
        List<CategoryTagDto> categoryTags = classes.getCategories() != null
                ? classes.getCategories().stream()
                .map(cat -> CategoryTagDto.builder()
                        .categoryId(cat.getCategoryId())
                        .name(cat.getName())
                        .color(cat.getColor())
                        .build())
                .toList()
                : List.of();

        // ⭐ Map mentors (nhiều mentor)
        List<UserResponseDto> mentorDtos = classes.getMentors() != null
                ? classes.getMentors().stream()
                .map(UserResponseDto::toDto)
                .toList()
                : List.of();

        return ClassLandingPageResponseDto.builder()
                .classId(classes.getClassId())
                .className(classes.getClassName())
                .description(classes.getDescription())
                .amount(classes.getAmount())
                .effectiveFrom(classes.getEffectiveFrom())
                .effectiveTo(classes.getEffectiveTo())
                .mentors(mentorDtos)
                .currentStudentCount(studentCount)
                .categories(categoryTags)
                .build();
    }
}
