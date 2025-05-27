package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.StudentAccountResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserResponseDto> GetAllStudent(int page, int size);
    StudentAccountResponseDto CreateStudent(UserCreateRequestDto user);
    UserResponseDto EditProfile(UserEditRequestDto user);
    void DeleteStudent(Long userId);
}
