package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.StudentStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class UserWithClassesDto {
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String status;
    private String studentStatus; // ✅
    private LocalDateTime createdAt;
    private LocalDate dateOfBirth;
    private List<EnrolledClassLiteDto> currentClasses;
}
