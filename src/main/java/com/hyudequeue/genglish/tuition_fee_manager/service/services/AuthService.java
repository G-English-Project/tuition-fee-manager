package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserAuthRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserAuthResponseDto;

public interface AuthService {
    public UserAuthResponseDto GetUserAuthorize(UserAuthRequestDto userRequest);
}
