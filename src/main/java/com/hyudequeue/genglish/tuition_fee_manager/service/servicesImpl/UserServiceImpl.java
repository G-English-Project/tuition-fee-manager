package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.EnrolledClassDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.StudentAccountResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.StudentProfileDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserWithClassDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassEnrollmentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.UserService;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PasswordUtils;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ClassEnrollmentRepository classEnrollmentRepository;

    public UserServiceImpl(UserRepository userRepository, ClassEnrollmentRepository classEnrollmentRepository) {
        this.userRepository = userRepository;
        this.classEnrollmentRepository = classEnrollmentRepository;
    }

    @Override
    public Page<UserWithClassDto> GetAllStudent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAllActiveStudentsWithCurrentClass(pageable);
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
    public UserResponseDto EditProfile(UserEditRequestDto userDto, Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getPasswordHash() != null) {
            String hashedPassword = BCrypt.withDefaults().hashToString(12, userDto.getPasswordHash().toCharArray());
            existingUser.setPasswordHash(hashedPassword);
        }

        if (userDto.getFullName() != null) {
            existingUser.setFullName(userDto.getFullName());
        }

        if (userDto.getPhone() != null) {
            existingUser.setPhone(userDto.getPhone());
        }

        existingUser.setUpdatedAt(userDto.getUpdatedAt() != null ? userDto.getUpdatedAt() : LocalDateTime.now());

        User updatedUser = userRepository.save(existingUser);
        return UserResponseDto.toDto(updatedUser);
    }


    @Override
    public void DeleteStudent(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Student not found"));
        user.setStatus(UserStatusEnum.DISABLED);
        userRepository.save(user);
    }

    @Override
    public StudentProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ClassEnrollment> enrollments = classEnrollmentRepository.findByUserUserId(userId);

        List<EnrolledClassDto> enrolledClassDtos = enrollments.stream()
                .map(e -> EnrolledClassDto.builder()
                        .classId(e.getClasses().getClassId())
                        .className(e.getClasses().getClassName())
                        .description(e.getClasses().getDescription())
                        .status(e.getClasses().getStatus().name())
                        .amount(e.getClasses().getAmount())
                        .effectiveFrom(e.getClasses().getEffectiveFrom())
                        .effectiveTo(e.getClasses().getEffectiveTo())
                        .enrolledAt(e.getEnrolledAt())
                        .unEnrolledAt(e.getUnEnrolledAt())
                        .build()
                )
                .toList();

        return StudentProfileDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .enrolledClasses(enrolledClassDtos)
                .build();
    }

    @Override
    public Page<UserWithClassDto> searchStudents(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.searchStudentsWithClassByKeyword(keyword, pageable);
    }

}
