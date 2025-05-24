package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Enrollment.response;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDto {
    private ClassResponseDto classes;
    private UserResponseDto user;
    private LocalDateTime enrolledAt;
    private LocalDateTime unEnrolledAt;

    public static EnrollmentResponseDto fromEntity(ClassEnrollment enrollment) {
        if (enrollment == null) return null;

        return new EnrollmentResponseDto(
                ClassResponseDto.fromEntity(enrollment.getClasses()),
                UserResponseDto.toDto(enrollment.getUser()),
                enrollment.getEnrolledAt(),
                enrollment.getUnEnrolledAt()
        );
    }
}
