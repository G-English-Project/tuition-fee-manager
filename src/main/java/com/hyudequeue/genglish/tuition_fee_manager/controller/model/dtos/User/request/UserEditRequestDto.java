package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEditRequestDto {
    private String email;
    private String phone;
    private String passwordHash;
    private String fullName;
    private LocalDateTime updatedAt;
    private LocalDate dateOfBirth;

    public User toEntity(User existingUser) {
        if (existingUser == null) return null;

        String hashedPassword = passwordHash != null
                ? BCrypt.withDefaults().hashToString(12, passwordHash.toCharArray())
                : existingUser.getPasswordHash();

        return User.builder()
                .email(this.email != null ? this.email : existingUser.getEmail())
                .phone(this.phone != null ? this.phone : existingUser.getPhone())
                .passwordHash(hashedPassword)
                .fullName(this.fullName != null ? this.fullName : existingUser.getFullName())
                .role(existingUser.getRole())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now())
                .dateOfBirth(this.dateOfBirth)
                .build();
    }
}
