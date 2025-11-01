package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.BulkUserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.*;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.StudentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<UserWithClassesDto> GetAllStudent(
            int page, int size, Long classId, String className, StudentStatusEnum studentStatus, String sortBy, String sortDir
    );

    UserResponseDto createUserByRole(UserCreateRequestDto req, RoleEnum role);
    UserResponseDto EditProfile(UserEditRequestDto user, Long userId);
    void DeleteStudent(Long userId);
    StudentProfileDto getUserProfile(Long userId);
    void changePassword(Long userId, String oldPassword, String newPassword);
    Page<UserWithClassesDto> searchStudents(String keyword, int page, int size);
    Page<UserResponseDto> getAllByRole(RoleEnum role, int page, int size);
    BulkUserCreateResponseDto createBulkStudents(BulkUserCreateRequestDto request);
    void updateStudentStatus(Long userId, StudentStatusEnum studentStatus);
}
