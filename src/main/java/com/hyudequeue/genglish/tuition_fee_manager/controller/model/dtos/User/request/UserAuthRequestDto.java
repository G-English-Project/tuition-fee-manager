package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request;

import lombok.Data;

@Data
public class UserAuthRequestDto {
    private String email;
    private String password;
}
