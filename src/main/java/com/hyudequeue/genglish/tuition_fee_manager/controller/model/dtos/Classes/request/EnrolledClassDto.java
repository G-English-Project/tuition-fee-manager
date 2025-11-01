package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledClassDto {

    private Long classId;
    private String className;
    private String description;
    private String status;
    private BigDecimal amount;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private LocalDateTime enrolledAt;
    private LocalDateTime unEnrolledAt;
}