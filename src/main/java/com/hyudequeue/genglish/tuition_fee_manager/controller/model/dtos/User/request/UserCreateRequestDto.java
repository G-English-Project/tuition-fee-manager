package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.StudentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {
    private String email;
    private String phone;
    private String fullName;
    private RoleEnum role;
    private UserStatusEnum status;
    private StudentStatusEnum studentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate dateOfBirth;

    public User toEntityWithPassword(String rawPasswordHash) {
        return User.builder()
                .email(this.email)
                .phone(this.phone)
                .fullName(this.fullName)
                .role(this.role)
                .status(this.status)
                .studentStatus(this.studentStatus)
                .passwordHash(rawPasswordHash)
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
                .updatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now())
                .dateOfBirth(this.dateOfBirth)
                .build();
    }
}
