package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordRequestDto {
    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 8, message = "Mật khẩu mới phải tối thiểu 8 ký tự, và khác mật khẩu cũ")
    private String newPassword;
}
