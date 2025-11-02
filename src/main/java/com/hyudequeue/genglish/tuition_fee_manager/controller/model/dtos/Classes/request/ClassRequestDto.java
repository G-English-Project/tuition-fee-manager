package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
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
public class ClassRequestDto {
    private String className;
    private String description;
    private BigDecimal amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    // ✅ Thêm categoryIds để gán class category
    private List<Long> categoryIds;

    public Classes toEntity() {
        return Classes.builder()
                .className(this.className)
                .description(this.description)
                .amount(this.amount)
                .effectiveFrom(this.effectiveFrom)
                .effectiveTo(this.effectiveTo)
                .status(ClassStatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
