package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUserCreateRequestDto {
    
    @NotEmpty(message = "Student list cannot be empty")
    @Size(max = 100, message = "Cannot create more than 100 users at once")
    @Valid
    private List<BulkStudentDto> students;
}