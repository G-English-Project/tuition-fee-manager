package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassResponseDtoWithCount {
    private Long classId;
    private String className;
    private String description;
    private ClassStatusEnum status;
    private Integer amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long currentStudentCount;

    public static ClassResponseDtoWithCount fromEntity(Classes c, long count) {
        if (c == null) return null;
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
        return dto;
    }
}
