package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.StudentAccountResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.UserService;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PasswordUtils;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserResponseDto> GetAllStudent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("status").ascending());
        Page<User> userPage = userRepository.findByRole(RoleEnum.STUDENT, pageable);
        List<UserResponseDto> dtoList = userPage
                .getContent()
                .stream()
                .map(UserResponseDto::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }

    @Override
    public StudentAccountResponseDto CreateStudent(UserCreateRequestDto user) {
        String randomPassword = PasswordUtils.generateRandomPassword(8, 12);
        String hashedPassword = BCrypt.withDefaults().hashToString(12, randomPassword.toCharArray());
        User userSave = user.toEntityWithPassword(hashedPassword);
        User createdUser = userRepository.save(userSave);
        createdUser.setPasswordHash(randomPassword);
        return StudentAccountResponseDto.toDto(createdUser);
    }

    @Override
    public UserResponseDto EditProfile(UserEditRequestDto user, Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),"Student not found"));
        User newUser = userRepository.save(user.toEntity(existingUser));
        return UserResponseDto.toDto(newUser);
    }

    @Override
    public void DeleteStudent(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Student not found"));
        user.setStatus(UserStatusEnum.DISABLED);
        userRepository.save(user);
    }
}
