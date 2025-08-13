package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.EnrolledClassDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.*;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassEnrollmentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.UserService;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.CommonConstants;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PasswordUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ClassEnrollmentRepository classEnrollmentRepository;

    public UserServiceImpl(UserRepository userRepository, ClassEnrollmentRepository classEnrollmentRepository) {
        this.userRepository = userRepository;
        this.classEnrollmentRepository = classEnrollmentRepository;
    }

    @Override
    public Page<UserWithClassesDto> GetAllStudent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> usersPage = userRepository
                .findByRoleAndStatusOrderByCreatedAtDesc(RoleEnum.STUDENT, UserStatusEnum.ACTIVE, pageable);

        if (usersPage.isEmpty()) {
            return usersPage.map(u -> null);
        }

        List<Long> userIds = usersPage.getContent().stream().map(User::getUserId).toList();
        List<ClassEnrollment> activeEnrollments = classEnrollmentRepository
                .findByUser_UserIdInAndUnEnrolledAtIsNull(userIds);

        Map<Long, List<ClassEnrollment>> byUserId = activeEnrollments.stream()
                .collect(java.util.stream.Collectors.groupingBy(e -> e.getUser().getUserId()));

        return usersPage.map(u -> {
            List<EnrolledClassLiteDto> currentClasses = byUserId.getOrDefault(u.getUserId(), List.of())
                    .stream()
                    .map(e -> EnrolledClassLiteDto.builder()
                            .classId(e.getClasses().getClassId())
                            .className(e.getClasses().getClassName())
                            .enrolledAt(e.getEnrolledAt())
                            .build())
                    .toList();

            return UserWithClassesDto.builder()
                    .userId(u.getUserId())
                    .email(u.getEmail())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .status(u.getStatus().name())
                    .createdAt(u.getCreatedAt())
                    .currentClasses(currentClasses)
                    .build();
        });
    }



    @Override
    public StudentAccountResponseDto CreateStudent(UserCreateRequestDto user) {
        String randomPassword = CommonConstants.STUDENT_DEFAULT_PASSWORD;
        String hashedPassword = BCrypt.withDefaults().hashToString(12, randomPassword.toCharArray());
        User userSave = user.toEntityWithPassword(hashedPassword);
        userSave.setChangedDefaultPassword(false);
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
    @Transactional
    public void DeleteStudent(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        LocalDateTime now = LocalDateTime.now();

        int affected = classEnrollmentRepository.unEnrollAllActiveByUser(userId, now);
        user.setStatus(UserStatusEnum.DISABLED);
        user.setUpdatedAt(now);
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
    public Page<UserWithClassesDto> searchStudents(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> usersPage = userRepository.searchStudents(RoleEnum.STUDENT, UserStatusEnum.ACTIVE, keyword, pageable);

        if (usersPage.isEmpty()) {
            return usersPage.map(u -> null);
        }

        List<Long> userIds = usersPage.getContent().stream().map(User::getUserId).toList();
        List<ClassEnrollment> activeEnrollments = classEnrollmentRepository
                .findByUser_UserIdInAndUnEnrolledAtIsNull(userIds);

        Map<Long, List<ClassEnrollment>> byUserId = activeEnrollments.stream()
                .collect(java.util.stream.Collectors.groupingBy(e -> e.getUser().getUserId()));

        return usersPage.map(u -> {
            List<EnrolledClassLiteDto> currentClasses = byUserId.getOrDefault(u.getUserId(), List.of())
                    .stream()
                    .map(e -> EnrolledClassLiteDto.builder()
                            .classId(e.getClasses().getClassId())
                            .className(e.getClasses().getClassName())
                            .enrolledAt(e.getEnrolledAt())
                            .build())
                    .toList();

            return UserWithClassesDto.builder()
                    .userId(u.getUserId())
                    .email(u.getEmail())
                    .fullName(u.getFullName())
                    .phone(u.getPhone())
                    .status(u.getStatus().name())
                    .createdAt(u.getCreatedAt())
                    .currentClasses(currentClasses)
                    .build();
        });
    }


    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var result = BCrypt.verifyer().verify(oldPassword.toCharArray(), user.getPasswordHash());
        if (!result.verified) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mật khẩu cũ không trùng khớp");
        }

        var sameAsOld = BCrypt.verifyer().verify(newPassword.toCharArray(), user.getPasswordHash()).verified;
        if (sameAsOld) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới phải khác mật khẩu cũ");
        }

        String hashed = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());
        user.setPasswordHash(hashed);
        user.setUpdatedAt(LocalDateTime.now());
        user.setChangedDefaultPassword(true);
        userRepository.save(user);
    }

}
