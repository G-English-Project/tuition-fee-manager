package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassFeeModifyRequestDto {
    private Long classId;
    private Integer amount;
}
