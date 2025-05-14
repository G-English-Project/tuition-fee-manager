package com.hyudequeue.genglish.tuition_fee_manager.model.dtos;

import lombok.Data;

@Data
public class UserAuthRequestDto {
    private String email;
    private String password;
}
