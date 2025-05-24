package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request;

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
public class ClassRequestDto {
    private String className;
    private String description;
    private Integer amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

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
