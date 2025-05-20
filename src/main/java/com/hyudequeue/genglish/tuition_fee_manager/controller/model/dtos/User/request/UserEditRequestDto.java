package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request;

import at.favre.lib.crypto.bcrypt.BCrypt;
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
public class UserEditRequestDto {
    private Long userId;
    private String email;
    private String passwordHash;
    private String fullName;
    private UserStatusEnum status;
    private LocalDateTime updatedAt;

    public User toEntity(User existingUser) {
        if (existingUser == null) return null;

        String hashedPassword = passwordHash != null
                ? BCrypt.withDefaults().hashToString(12, passwordHash.toCharArray())
                : existingUser.getPasswordHash();

        return User.builder()
                .userId(existingUser.getUserId())
                .email(this.email != null ? this.email : existingUser.getEmail())
                .passwordHash(hashedPassword)
                .fullName(this.fullName != null ? this.fullName : existingUser.getFullName())
                .role(existingUser.getRole())
                .status(this.status != null ? this.status : existingUser.getStatus())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now())
                .build();
    }
}
