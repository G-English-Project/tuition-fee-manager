package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;


import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserInClassWithNoteDto {
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime unEnrolledAt;
    private LocalDate dateOfBirth;
    private String note;
    public static UserInClassWithNoteDto fromEnrollment(ClassEnrollment e) {
        var u = e.getUser();
        return UserInClassWithNoteDto.builder()
                .userId(u.getUserId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .status(u.getStatus().name())
                .enrolledAt(e.getEnrolledAt())
                .unEnrolledAt(e.getUnEnrolledAt())
                .dateOfBirth(e.getUser().getDateOfBirth())
                .note(e.getNote())
                .build();
    }
}