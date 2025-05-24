package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassFeeModifyRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Enrollment.response.EnrollmentResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassEnrollmentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ClassService;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ClassServiceImpl implements ClassService {

    private final ClassEnrollmentRepository classEnrollmentRepository;
    private final ClassRepository classesRepository;
    private final UserRepository userRepository;

    public ClassServiceImpl(ClassEnrollmentRepository classEnrollmentRepository, ClassRepository classesRepository, UserRepository userRepository) {
        this.classEnrollmentRepository = classEnrollmentRepository;
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserResponseDto> GetCurrentStudentInClass(Long classId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("user.fullName").ascending());
        Page<ClassEnrollment> enrollments = classEnrollmentRepository
                .findByClasses_ClassIdAndUnEnrolledAtIsNull(classId, pageable);

        return enrollments.map(enrollment -> UserResponseDto.toDto(enrollment.getUser()));
    }

    @Override
    public Page<UserResponseDto> GetAllStudentInClass(Long classId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("user.fullName").ascending());
        Page<ClassEnrollment> enrollments = classEnrollmentRepository
                .findByClasses_ClassId(classId, pageable);

        return enrollments.map(enrollment -> UserResponseDto.toDto(enrollment.getUser()));
    }

    @Override
    public Page<ClassResponseDto> GetAllClasses(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        return classesRepository.findAll(pageable)
                .map(ClassResponseDto::fromEntity);
    }

    @Override
    public Page<EnrollmentResponseDto> GetStudentEnrollmentClasses(Long studentId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("enrolledAt").descending());
        Page<ClassEnrollment> enrollments = classEnrollmentRepository.findByUser_UserId(studentId, pageable);

        return enrollments.map(EnrollmentResponseDto::fromEntity);
    }


    @Override
    public ClassResponseDto GetClassById(Long classId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),"Class not found"));
        return ClassResponseDto.fromEntity(classes);
    }

    @Override
    public ClassResponseDto CreateClass(ClassRequestDto classCreate) {
        return ClassResponseDto.fromEntity(classesRepository.save(classCreate.toEntity()));
    }

    @Override
    public ClassResponseDto EditClass(ClassRequestDto classEdit) {
        return ClassResponseDto.fromEntity(classesRepository.save(classEdit.toEntity()));
    }

    @Override
    public void RemoveClass(Long classId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),"Class not found"));
        classes.setStatus(ClassStatusEnum.INACTIVE);
        classesRepository.save(classes);
    }
    @Override
    public ClassResponseDto ModifyClassFee(ClassFeeModifyRequestDto classFeeModify) {
        return null;
    }

    @Override
    public EnrollmentResponseDto AssignStudentToClass(Long classId, Long studentId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Class not found"));

        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Student not found"));

        boolean alreadyEnrolled = classEnrollmentRepository
                .findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(classId, studentId)
                .isPresent();

        if (alreadyEnrolled) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), "Student is already enrolled in this class");
        }

        ClassEnrollment newEnrollment = new ClassEnrollment();
        newEnrollment.setEnrolledAt(LocalDateTime.now());
        newEnrollment.setClasses(classes);
        newEnrollment.setUser(user);

        return EnrollmentResponseDto.fromEntity(classEnrollmentRepository.save(newEnrollment));
    }
    @Override
    public void RemoveStudentFromClass(Long classId, Long studentId) {
        ClassEnrollment enrollment = classEnrollmentRepository
                .findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found or already unenrolled"));

        enrollment.setUnEnrolledAt(LocalDateTime.now());
        classEnrollmentRepository.save(enrollment);
    }

}
