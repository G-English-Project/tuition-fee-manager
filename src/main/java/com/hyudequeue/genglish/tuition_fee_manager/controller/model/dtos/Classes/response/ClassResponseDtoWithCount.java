package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
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
public class ClassResponseDtoWithCount {
    private Long classId;
    private String className;
    private String description;
    private ClassStatusEnum status;
    private BigDecimal amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long currentStudentCount;

    private List<ClassCategoryDTO> categories;

    // ✅ New field
    private MentorDto mentor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MentorDto {
        private Long userId;
        private String fullName;
    }

    public static ClassResponseDtoWithCount fromEntity(Classes c, long count) {
        ClassResponseDtoWithCount dto = new ClassResponseDtoWithCount();
        dto.setClassId(c.getClassId());
        dto.setClassName(c.getClassName());
        dto.setDescription(c.getDescription());
        dto.setStatus(c.getStatus());
        dto.setAmount(c.getAmount());
        dto.setEffectiveFrom(c.getEffectiveFrom());
        dto.setEffectiveTo(c.getEffectiveTo());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        dto.setCurrentStudentCount(count);

        if (c.getCategories() != null) {
            dto.setCategories(
                    c.getCategories().stream()
                            .map(ClassCategoryDTO::fromEntity)
                            .toList()
            );
        }

        // ✅ Map mentor
        if (c.getMentorBy() != null) {
            dto.setMentor(new MentorDto(
                    c.getMentorBy().getUserId(),
                    c.getMentorBy().getFullName()
            ));
        }

        return dto;
    }
}

