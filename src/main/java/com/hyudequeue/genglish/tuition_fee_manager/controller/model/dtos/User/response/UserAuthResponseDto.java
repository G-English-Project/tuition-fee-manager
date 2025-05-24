package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponseDto {
    private String token;
    private String tokenType;
    private UserResponseDto user;
}
