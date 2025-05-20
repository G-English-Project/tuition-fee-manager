package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request;

import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.utility.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.Enums.UserStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {
    private String email;
    private String fullName;
    private RoleEnum role;
    private UserStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User toEntityWithPassword(String rawPasswordHash) {
        return User.builder()
                .email(this.email)
                .fullName(this.fullName)
                .role(this.role)
                .status(this.status)
                .passwordHash(rawPasswordHash)
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
                .updatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now())
                .build();
    }
}
