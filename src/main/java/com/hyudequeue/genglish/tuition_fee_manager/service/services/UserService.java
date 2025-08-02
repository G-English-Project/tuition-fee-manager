package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.StudentAccountResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.StudentProfileDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserWithClassDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserWithClassDto> GetAllStudent(int page, int size);
    StudentAccountResponseDto CreateStudent(UserCreateRequestDto user);
    UserResponseDto EditProfile(UserEditRequestDto user, Long userId);
    void DeleteStudent(Long userId);
    StudentProfileDto getUserProfile(Long userId);

    Page<UserWithClassDto> searchStudents(String keyword, int page, int size);

}
