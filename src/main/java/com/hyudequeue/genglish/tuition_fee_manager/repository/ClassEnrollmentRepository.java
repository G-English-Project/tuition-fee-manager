package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {
    Page<ClassEnrollment> findByClasses_ClassIdAndUnEnrolledAtIsNull(Long classId, Pageable pageable);
    Page<ClassEnrollment> findByClasses_ClassId(Long classId, Pageable pageable);

    Page<ClassEnrollment> findByUser_UserId(Long userId, Pageable pageable);
    Optional<ClassEnrollment> findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(Long classId, Long userId);
}
