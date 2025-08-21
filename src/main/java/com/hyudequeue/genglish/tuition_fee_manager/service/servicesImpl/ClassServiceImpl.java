package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassFeeModifyRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassCountProjection;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassResponseDtoWithCount;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Enrollment.response.EnrollmentResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserInClassWithNoteDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassEnrollmentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ClassService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl implements ClassService {

    private final ClassEnrollmentRepository classEnrollmentRepository;
    private final ClassRepository classesRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ClassRepository classRepository;
    private final EmailServiceImpl emailService;
    private final InvoiceNotificationServiceImpl invoiceNotificationService;
    public ClassServiceImpl(ClassEnrollmentRepository classEnrollmentRepository, ClassRepository classesRepository, UserRepository userRepository, NotificationService notificationService, ClassRepository classRepository, EmailServiceImpl emailService, InvoiceNotificationServiceImpl invoiceNotificationService) {
        this.classEnrollmentRepository = classEnrollmentRepository;
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.classRepository = classRepository;
        this.emailService = emailService;
        this.invoiceNotificationService = invoiceNotificationService;
    }

    @Override
    public Page<UserInClassWithNoteDto> GetCurrentStudentInClass(Long classId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("user.fullName").ascending());
        Page<ClassEnrollment> enrollments = classEnrollmentRepository
                .findByClasses_ClassIdAndUnEnrolledAtIsNull(classId, pageable);

        return enrollments.map(UserInClassWithNoteDto::fromEnrollment);
    }

    @Override
    public Page<UserInClassWithNoteDto> GetAllStudentInClass(Long classId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("user.fullName").ascending());
        Page<ClassEnrollment> enrollments = classEnrollmentRepository
                .findByClasses_ClassId(classId, pageable);

        return enrollments.map(UserInClassWithNoteDto::fromEnrollment);
    }

    @Override
    public Page<ClassResponseDtoWithCount> GetAllClasses(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<Classes> page = classesRepository.findAll(pageable);

        List<Long> classIds = page.getContent().stream()
                .map(Classes::getClassId)
                .toList();

        Map<Long, Long> countMap = classIds.isEmpty()
                ? Map.of()
                : classEnrollmentRepository.countActiveByClassIds(classIds).stream()
                .collect(Collectors.toMap(ClassCountProjection::getClassId, ClassCountProjection::getCnt));

        return page.map(c -> ClassResponseDtoWithCount.fromEntity(c, countMap.getOrDefault(c.getClassId(), 0L)));
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
    public ClassResponseDto EditClass(Long classId, ClassRequestDto classEdit) {
        Classes existingClass = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        boolean isUpdated = false;

        if (classEdit.getClassName() != null && !classEdit.getClassName().equals(existingClass.getClassName())) {
            existingClass.setClassName(classEdit.getClassName());
            isUpdated = true;
        }

        if (classEdit.getDescription() != null && !classEdit.getDescription().equals(existingClass.getDescription())) {
            existingClass.setDescription(classEdit.getDescription());
            isUpdated = true;
        }

        if (classEdit.getAmount() != null && !classEdit.getAmount().equals(existingClass.getAmount())) {
            existingClass.setAmount(classEdit.getAmount());
            isUpdated = true;
        }

        if (classEdit.getEffectiveFrom() != null && !classEdit.getEffectiveFrom().equals(existingClass.getEffectiveFrom())) {
            existingClass.setEffectiveFrom(classEdit.getEffectiveFrom());
            isUpdated = true;
        }

        if (classEdit.getEffectiveTo() != null && !classEdit.getEffectiveTo().equals(existingClass.getEffectiveTo())) {
            existingClass.setEffectiveTo(classEdit.getEffectiveTo());
            isUpdated = true;
        }

        if (isUpdated) {
            existingClass.setUpdatedAt(LocalDateTime.now());
            classesRepository.save(existingClass);
        }

        return ClassResponseDto.fromEntity(existingClass);
    }

    @Override
    public void RemoveClass(Long classId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404),"Class not found"));
        classes.setStatus(ClassStatusEnum.INACTIVE);
        classesRepository.save(classes);
    }
    @Override
    public ClassResponseDto ModifyClassFee(ClassFeeModifyRequestDto req) {
        if (req == null || req.getClassId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "classId is required");
        }
        if (req.getAmount() == null || req.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be greater than 0");
        }

        Classes classes = classesRepository.findById(req.getClassId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        classes.setAmount(req.getAmount());
        classes.setUpdatedAt(LocalDateTime.now());
        classesRepository.save(classes);
        invoiceNotificationService.notifyClassFeeUpdated(classes, req.getAmount());
        return ClassResponseDto.fromEntity(classes);
    }



    @Override
    public EnrollmentResponseDto AssignStudentToClass(Long classId, Long studentId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        boolean alreadyEnrolled = classEnrollmentRepository
                .findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(classId, studentId)
                .isPresent();

        if (alreadyEnrolled) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student already enrolled in this class");
        }

        ClassEnrollment newEnrollment = ClassEnrollment.builder()
                .user(user)
                .classes(classes)
                .enrolledAt(LocalDateTime.now())
                .build();

        newEnrollment = classEnrollmentRepository.save(newEnrollment);

        invoiceNotificationService.notifyStudentAssignedToClass(user, classes);

        return EnrollmentResponseDto.fromEntity(newEnrollment);
    }



    @Override
    public void RemoveStudentFromClass(Long classId, Long studentId) {
        // Find the enrollment record
        ClassEnrollment enrollment = classEnrollmentRepository
                .findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found or already unenrolled"));

        // Mark as unenrolled
        enrollment.setUnEnrolledAt(LocalDateTime.now());

        // Fetch user (student) and class info for notification
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Classes classes = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        invoiceNotificationService.notifyStudentRemovedFromClass(user, classes);

        // Save the updated enrollment
        classEnrollmentRepository.save(enrollment);
    }


    @Override
    public UserInClassWithNoteDto NoteAStudentInClass(Long classId, Long studentId, String note) {
        ClassEnrollment enrollment = classEnrollmentRepository
                .findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(classId, studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Enrollment not found or already un-enrolled"));

        enrollment.setNote(note);
        enrollment = classEnrollmentRepository.save(enrollment);
        return UserInClassWithNoteDto.fromEnrollment(enrollment);
    }
}
