package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.EnrolledClassDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileDto {

    private Long userId;
    private String email;
    private String phone;
    private String fullName;
    private String role;
    private String status;
    private LocalDateTime createdAt;

    private List<EnrolledClassDto> enrolledClasses;
}