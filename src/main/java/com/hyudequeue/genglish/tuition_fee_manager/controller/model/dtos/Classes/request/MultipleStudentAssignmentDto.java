package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleStudentAssignmentDto {
    private List<Long> studentIds;
}