package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.BulkStudentCreateAndAssignDto;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassFeeModifyRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.*;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Enrollment.response.EnrollmentResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserInClassWithNoteDto;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.StudentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassCategoryRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassEnrollmentRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ClassService;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final ClassCategoryRepository classCategoryRepository;
    public ClassServiceImpl(ClassEnrollmentRepository classEnrollmentRepository, ClassRepository classesRepository, UserRepository userRepository, NotificationService notificationService, ClassRepository classRepository, EmailServiceImpl emailService, InvoiceNotificationServiceImpl invoiceNotificationService, ClassCategoryRepository classCategoryRepository) {
        this.classEnrollmentRepository = classEnrollmentRepository;
        this.classesRepository = classesRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.classRepository = classRepository;
        this.emailService = emailService;
        this.invoiceNotificationService = invoiceNotificationService;
        this.classCategoryRepository = classCategoryRepository;
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
    public Page<ClassResponseDtoWithCount> GetAllClasses(
            int pageNumber,
            int pageSize,
            LocalDate effectiveFrom,
            ClassStatusEnum status,
            String prioritizedCategoryName
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Specification<Classes> spec = Specification.where(null);

        if (effectiveFrom != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("effectiveFrom"), effectiveFrom));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (prioritizedCategoryName != null && !prioritizedCategoryName.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Object, Object> categoryJoin = root.join("categories", JoinType.INNER);
                return cb.equal(cb.lower(categoryJoin.get("name")), prioritizedCategoryName.toLowerCase());
            });
        }

        spec = spec.and((root, query, cb) -> {
            Join<Object, Object> categoryJoin = root.join("categories", JoinType.LEFT);
            query.orderBy(
                    cb.asc(categoryJoin.get("name")),
                    cb.asc(root.get("className"))
            );
            return null;
        });

        Page<Classes> page = classesRepository.findAll(spec, pageable);

        List<Long> classIds = page.getContent().stream()
                .map(Classes::getClassId)
                .toList();

        Map<Long, Long> countMap = classIds.isEmpty()
                ? Map.of()
                : classEnrollmentRepository.countActiveByClassIds(classIds).stream()
                .collect(Collectors.toMap(ClassCountProjection::getClassId, ClassCountProjection::getCnt));

        return page.map(c -> ClassResponseDtoWithCount.fromEntity(
                c,
                countMap.getOrDefault(c.getClassId(), 0L)
        ));
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
        // Convert DTO to entity
        Classes newClass = classCreate.toEntity();

        // ✅ Gán category nếu có
        if (classCreate.getCategoryIds() != null && !classCreate.getCategoryIds().isEmpty()) {
            List<ClassCategory> categories = classCategoryRepository.findAllById(classCreate.getCategoryIds());
            newClass.setCategories(categories);
        }

        // Save và return DTO
        return ClassResponseDto.fromEntity(classesRepository.save(newClass));
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

        // ✅ Cập nhật class category (thêm hoặc bỏ gán)
        if (classEdit.getCategoryIds() != null) {
            List<ClassCategory> categories = classCategoryRepository.findAllById(classEdit.getCategoryIds());
            existingClass.setCategories(categories); // gán mới, bỏ những category cũ không có trong list
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

        classes.setAmount(BigDecimal.valueOf(req.getAmount()));
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

        // ✅ FIX: Nếu học viên được gán lớp → ON ACTIVE
        if (user.getStudentStatus() == null || user.getStudentStatus() != StudentStatusEnum.ACTIVE) {
            user.setStudentStatus(StudentStatusEnum.ACTIVE);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
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
        ClassEnrollment enrollment = classEnrollmentRepository
                .findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(classId, studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found or already un-enrolled"));

        enrollment.setUnEnrolledAt(LocalDateTime.now());
        classEnrollmentRepository.save(enrollment);

        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // check xem có lớp nào khác không
        boolean stillHasActiveClass = classEnrollmentRepository
                .existsByUser_UserIdAndUnEnrolledAtIsNull(studentId);

        if (!stillHasActiveClass) {
            user.setStudentStatus(StudentStatusEnum.INACTIVE);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
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

    @Override
    public MultipleStudentAssignmentResponseDto AssignMultipleStudentsToClass(Long classId, List<Long> studentIds) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        List<EnrollmentResponseDto> successfulEnrollments = new ArrayList<>();
        List<MultipleStudentAssignmentResponseDto.FailedAssignmentDto> failedAssignments = new ArrayList<>();

        for (Long studentId : studentIds) {
            try {
                User user = userRepository.findById(studentId).orElse(null);
                if (user == null) {
                    failedAssignments.add(
                            MultipleStudentAssignmentResponseDto.FailedAssignmentDto.builder()
                                    .studentId(studentId)
                                    .reason("Student not found")
                                    .studentName("Unknown")
                                    .build()
                    );
                    continue;
                }

                if (user.getRole() != RoleEnum.STUDENT) {
                    failedAssignments.add(
                            MultipleStudentAssignmentResponseDto.FailedAssignmentDto.builder()
                                    .studentId(studentId)
                                    .reason("User is not a student")
                                    .studentName(user.getFullName())
                                    .build()
                    );
                    continue;
                }

                // ✅ SET student_status = ACTIVE nếu chưa có hoặc khác
                if (user.getStudentStatus() == null || user.getStudentStatus() != StudentStatusEnum.ACTIVE) {
                    user.setStudentStatus(StudentStatusEnum.ACTIVE);
                    userRepository.save(user);
                }

                boolean alreadyEnrolled = classEnrollmentRepository
                        .findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(classId, studentId)
                        .isPresent();

                if (alreadyEnrolled) {
                    failedAssignments.add(
                            MultipleStudentAssignmentResponseDto.FailedAssignmentDto.builder()
                                    .studentId(studentId)
                                    .reason("Student already enrolled in this class")
                                    .studentName(user.getFullName())
                                    .build()
                    );
                    continue;
                }

                ClassEnrollment newEnrollment = ClassEnrollment.builder()
                        .user(user)
                        .classes(classes)
                        .enrolledAt(LocalDateTime.now())
                        .build();

                newEnrollment = classEnrollmentRepository.save(newEnrollment);

                try {
                    invoiceNotificationService.notifyStudentAssignedToClass(user, classes);
                } catch (Exception e) {
                    System.err.println("Failed to send notification for student " + studentId + ": " + e.getMessage());
                }

                successfulEnrollments.add(EnrollmentResponseDto.fromEntity(newEnrollment));

            } catch (Exception e) {
                failedAssignments.add(
                        MultipleStudentAssignmentResponseDto.FailedAssignmentDto.builder()
                                .studentId(studentId)
                                .reason("Unexpected error: " + e.getMessage())
                                .studentName("Unknown")
                                .build()
                );
            }
        }

        return MultipleStudentAssignmentResponseDto.builder()
                .successfulEnrollments(successfulEnrollments)
                .failedAssignments(failedAssignments)
                .totalProcessed(studentIds.size())
                .successCount(successfulEnrollments.size())
                .failedCount(failedAssignments.size())
                .build();
    }


    @Override
    public void RestoreClass(Long classId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        if (classes.getStatus() != ClassStatusEnum.ACTIVE) {
            classes.setStatus(ClassStatusEnum.ACTIVE);
            classes.setUpdatedAt(LocalDateTime.now());
            classesRepository.save(classes);
        }
    }


    @Override
    public List<ClassResponseDto> GetClassesByCategory(Long categoryId) {
        return classRepository.findByCategories_CategoryId(categoryId)
                .stream()
                .map(ClassResponseDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public BulkStudentCreateAndAssignResponseDto bulkCreateStudentsAndAssignToClass(BulkStudentCreateAndAssignDto request) {
        // Validate class exists
        Classes classes = classesRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        List<BulkStudentCreateAndAssignResponseDto.SuccessfulStudent> successfulStudents = new ArrayList<>();
        List<BulkStudentCreateAndAssignResponseDto.FailedStudent> failedStudents = new ArrayList<>();

        for (BulkStudentCreateAndAssignDto.StudentData studentData : request.getStudents()) {
            try {
                // Validate email
                if (studentData.getEmail() == null || studentData.getEmail().trim().isEmpty()) {
                    failedStudents.add(
                            BulkStudentCreateAndAssignResponseDto.FailedStudent.builder()
                                    .email(studentData.getEmail())
                                    .fullName(studentData.getFullName())
                                    .reason("Email is required")
                                    .build()
                    );
                    continue;
                }

                // Validate dateOfBirth is required (vì password là ngày sinh)
                if (studentData.getDateOfBirth() == null) {
                    failedStudents.add(
                            BulkStudentCreateAndAssignResponseDto.FailedStudent.builder()
                                    .email(studentData.getEmail())
                                    .fullName(studentData.getFullName())
                                    .reason("Date of birth is required for password generation")
                                    .build()
                    );
                    continue;
                }

                // Check if email already exists
                Optional<User> existingUser = userRepository.findByEmail(studentData.getEmail());
                if (existingUser.isPresent()) {
                    failedStudents.add(
                            BulkStudentCreateAndAssignResponseDto.FailedStudent.builder()
                                    .email(studentData.getEmail())
                                    .fullName(studentData.getFullName())
                                    .reason("Email already exists")
                                    .build()
                    );
                    continue;
                }

                // Password luôn luôn là ngày tháng năm sinh (ddMMyyyy)
                String defaultPassword = studentData.getDateOfBirth().format(DateTimeFormatter.ofPattern("ddMMyyyy"));

                // Hash password
                String hashedPassword = BCrypt.withDefaults().hashToString(12, defaultPassword.toCharArray());

                // Create user entity
                User newUser = User.builder()
                        .email(studentData.getEmail())
                        .fullName(studentData.getFullName())
                        .phone(studentData.getPhone())
                        .dateOfBirth(studentData.getDateOfBirth())
                        .passwordHash(hashedPassword)
                        .role(RoleEnum.STUDENT)
                        .status(UserStatusEnum.ACTIVE)
                        .studentStatus(StudentStatusEnum.ACTIVE)
                        .changedDefaultPassword(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                // Save user
                User savedUser = userRepository.save(newUser);

                // Check if already enrolled (shouldn't happen, but just in case)
                boolean alreadyEnrolled = classEnrollmentRepository
                        .findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(request.getClassId(), savedUser.getUserId())
                        .isPresent();

                if (alreadyEnrolled) {
                    failedStudents.add(
                            BulkStudentCreateAndAssignResponseDto.FailedStudent.builder()
                                    .email(studentData.getEmail())
                                    .fullName(studentData.getFullName())
                                    .reason("User created but already enrolled in class")
                                    .build()
                    );
                    continue;
                }

                // Create enrollment
                ClassEnrollment enrollment = ClassEnrollment.builder()
                        .user(savedUser)
                        .classes(classes)
                        .enrolledAt(LocalDateTime.now())
                        .build();

                ClassEnrollment savedEnrollment = classEnrollmentRepository.save(enrollment);

                // Send notification (don't fail if notification fails)
                try {
                    invoiceNotificationService.notifyStudentAssignedToClass(savedUser, classes);
                } catch (Exception e) {
                    System.err.println("Failed to send notification for student " + savedUser.getEmail() + ": " + e.getMessage());
                }

                // Add to successful list
                successfulStudents.add(
                        BulkStudentCreateAndAssignResponseDto.SuccessfulStudent.builder()
                                .userId(savedUser.getUserId())
                                .email(savedUser.getEmail())
                                .fullName(savedUser.getFullName())
                                .enrollmentId(savedEnrollment.getEnrollmentId())
                                .message("Student created and enrolled successfully")
                                .build()
                );

            } catch (Exception e) {
                // Handle unexpected errors
                failedStudents.add(
                        BulkStudentCreateAndAssignResponseDto.FailedStudent.builder()
                                .email(studentData.getEmail())
                                .fullName(studentData.getFullName())
                                .reason("Unexpected error: " + e.getMessage())
                                .build()
                );
            }
        }

        return BulkStudentCreateAndAssignResponseDto.builder()
                .successfulStudents(successfulStudents)
                .failedStudents(failedStudents)
                .totalProcessed(request.getStudents().size())
                .successCount(successfulStudents.size())
                .failedCount(failedStudents.size())
                .build();
    }

    @Override
    public MonthlyRevenueResponseDto GetMonthlyRevenue(Long classId, LocalDate fromDate, LocalDate toDate) {
        // Validate class exists
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Class not found"));

        List<MonthlyRevenueResponseDto.MonthlyRevenueDetail> monthlyRevenues = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        // Iterate through each month in the range
        LocalDate currentMonth = fromDate.withDayOfMonth(1);
        LocalDate endMonth = toDate.withDayOfMonth(1);

        while (!currentMonth.isAfter(endMonth)) {
            LocalDate firstDayOfMonth = currentMonth;
            LocalDate lastDayOfMonth = currentMonth.withDayOfMonth(
                    currentMonth.lengthOfMonth()
            );

            // Count active students in this month
            Long activeCount = classEnrollmentRepository.countActiveStudentsInMonth(
                    classId,
                    firstDayOfMonth.atStartOfDay(),
                    lastDayOfMonth.atTime(23, 59, 59)
            );

            // ✅ FIX: Convert Long to BigDecimal before multiply
            BigDecimal monthlyRevenue = classes.getAmount()
                    .multiply(BigDecimal.valueOf(activeCount)); // Sử dụng BigDecimal.valueOf()

            totalRevenue = totalRevenue.add(monthlyRevenue);

            monthlyRevenues.add(
                    MonthlyRevenueResponseDto.MonthlyRevenueDetail.builder()
                            .year(currentMonth.getYear())
                            .month(currentMonth.getMonthValue())
                            .monthName(currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .activeStudentCount(activeCount)
                            .revenue(monthlyRevenue)
                            .build()
            );

            currentMonth = currentMonth.plusMonths(1);
        }

        return MonthlyRevenueResponseDto.builder()
                .classId(classes.getClassId())
                .className(classes.getClassName())
                .currentClassFee(classes.getAmount())
                .monthlyRevenues(monthlyRevenues)
                .totalRevenue(totalRevenue)
                .build();
    }

    @Override
    public ClassResponseDto assignMentor(Long classId, Long mentorId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (mentor.getRole() != RoleEnum.TEACHER && mentor.getRole() != RoleEnum.TA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must be TEACHER or TA");
        }

        classes.setMentorBy(mentor);
        classes.setUpdatedAt(LocalDateTime.now());
        classesRepository.save(classes);

        return ClassResponseDto.fromEntity(classes);
    }
    @Override
    public void removeMentor(Long classId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found"));

        classes.setMentorBy(null);
        classes.setUpdatedAt(LocalDateTime.now());

        classesRepository.save(classes);
    }
    @Override
    public List<ClassResponseDto> getClassesByTeacherId(Long teacherId, Long categoryId) {
        List<Classes> classes = classRepository.findByMentorBy_UserId(teacherId);
        
        if (categoryId != null) {
            classes = classes.stream()
                    .filter(c -> c.getCategories() != null && 
                            c.getCategories().stream()
                                    .anyMatch(cat -> cat.getCategoryId().equals(categoryId)))
                    .toList();
        }
        
        List<Long> classIds = classes.stream().map(Classes::getClassId).toList();
        
        Map<Long, Long> studentCountMap = new HashMap<>();
        if (!classIds.isEmpty()) {
            List<ClassCountProjection> counts = classEnrollmentRepository.countActiveByClassIds(classIds);
            studentCountMap = counts.stream()
                    .collect(Collectors.toMap(ClassCountProjection::getClassId, ClassCountProjection::getCnt));
        }
        
        Map<Long, Long> finalCountMap = studentCountMap;
        return classes.stream()
                .map(c -> {
                    ClassResponseDto dto = ClassResponseDto.fromEntity(c);
                    dto.setCurrentStudentCount(finalCountMap.getOrDefault(c.getClassId(), 0L).intValue());
                    return dto;
                })
                .toList();
    }
    @Override
    public Page<ClassLandingPageResponseDto> getActiveClassesForLandingPage(Pageable pageable) {
        // Lấy các lớp đang ACTIVE, sort theo effectiveFrom DESC
        Specification<Classes> spec = Specification.where(null);

        // Chỉ lấy lớp ACTIVE
        spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), ClassStatusEnum.ACTIVE));

        // Sort theo effectiveFrom DESC (lớp mới nhất trước)
        spec = spec.and((root, query, cb) -> {
            query.orderBy(cb.desc(root.get("effectiveFrom")));
            return null;
        });

        Page<Classes> classesPage = classesRepository.findAll(spec, pageable);

        // Lấy số lượng học viên cho mỗi lớp
        List<Long> classIds = classesPage.getContent().stream()
                .map(Classes::getClassId)
                .toList();

        Map<Long, Long> countMap = classIds.isEmpty()
                ? Map.of()
                : classEnrollmentRepository.countActiveByClassIds(classIds).stream()
                .collect(Collectors.toMap(ClassCountProjection::getClassId, ClassCountProjection::getCnt));

        return classesPage.map(c -> ClassLandingPageResponseDto.fromEntity(
                c,
                countMap.getOrDefault(c.getClassId(), 0L).intValue()
        ));
    }
}
