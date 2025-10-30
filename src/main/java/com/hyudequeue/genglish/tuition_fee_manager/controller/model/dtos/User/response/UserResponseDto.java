package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.StudentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.GenerateId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String shownId;
    private String email;
    private String phone;
    private String fullName;
    private RoleEnum role;
    private UserStatusEnum status;
    private StudentStatusEnum studentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean changedDefaultPassword;
    private LocalDate dateOfBirth;

    public static UserResponseDto toDto(User user) {
        if (user == null) return null;

        return new UserResponseDto(
                user.getUserId(),
                GenerateId.formatId(user.getUserId()),
                user.getEmail(),
                user.getPhone(),
                user.getFullName(),
                user.getRole(),
                user.getStatus(),
                user.getStudentStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isChangedDefaultPassword(),
                user.getDateOfBirth()
        );
    }
}
