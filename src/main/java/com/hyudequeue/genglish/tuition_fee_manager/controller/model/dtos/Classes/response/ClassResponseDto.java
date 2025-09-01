package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
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

    public static ClassResponseDto fromEntity(Classes classes) {
        if (classes == null) return null;

        return new ClassResponseDto(
                classes.getClassId(),
                classes.getShownId(),
                classes.getClassName(),
                classes.getDescription(),
                classes.getStatus(),
                classes.getAmount(),
                classes.getEffectiveFrom(),
                classes.getEffectiveTo(),
                classes.getCreatedAt(),
                classes.getUpdatedAt()
        );
    }

}
