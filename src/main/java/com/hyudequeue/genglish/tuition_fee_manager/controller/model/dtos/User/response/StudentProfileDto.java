package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.EnrolledClassDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileDto {

    private Long userId;
    private String shownId;
    private String email;
    private String phone;
    private String fullName;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDate dateOfBirth;

    private List<EnrolledClassDto> enrolledClasses;
}