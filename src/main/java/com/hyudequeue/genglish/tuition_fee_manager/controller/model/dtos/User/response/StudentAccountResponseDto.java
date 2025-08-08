package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAccountResponseDto {
    private Long userId;
    private String email;
    private String phone;
    private String fullName;
    private RoleEnum role;
    private String password;
    private UserStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudentAccountResponseDto toDto(User user) {
        if (user == null) return null;

        return new StudentAccountResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getPhone(),
                user.getFullName(),
                user.getRole(),
                user.getPasswordHash(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
