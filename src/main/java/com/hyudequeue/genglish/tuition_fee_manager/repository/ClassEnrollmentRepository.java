package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassCountProjection;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {
    Page<ClassEnrollment> findByClasses_ClassIdAndUnEnrolledAtIsNull(Long classId, Pageable pageable);
    Page<ClassEnrollment> findByClasses_ClassId(Long classId, Pageable pageable);
    List<ClassEnrollment> findByUser_UserIdInAndUnEnrolledAtIsNull(List<Long> userIds);
    Page<ClassEnrollment> findByUser_UserId(Long userId, Pageable pageable);
    Optional<ClassEnrollment> findByClasses_ClassIdAndUser_UserIdAndUnEnrolledAtIsNull(Long classId, Long userId);
    List<ClassEnrollment> findByUserUserId(Long userId);

    @Query("SELECT ce FROM ClassEnrollment ce " +
            "WHERE ce.classes.classId = :classId " +
            "AND ce.user.userId IN :userIds " +
            "AND ce.unEnrolledAt IS NULL")
    List<ClassEnrollment> findActiveByClassAndUserIds(@Param("classId") Long classId,
                                                      @Param("userIds") List<Long> userIds);
    @Modifying
    @Query("UPDATE ClassEnrollment ce " +
            "SET ce.unEnrolledAt = :ts " +
            "WHERE ce.user.userId = :userId AND ce.unEnrolledAt IS NULL")
    int unEnrollAllActiveByUser(@Param("userId") Long userId, @Param("ts") LocalDateTime ts);

    @Query("""
        SELECT ce.classes.classId AS classId, COUNT(ce) AS cnt
        FROM ClassEnrollment ce
        WHERE ce.classes.classId IN :classIds
          AND ce.unEnrolledAt IS NULL
        GROUP BY ce.classes.classId
    """)
    List<ClassCountProjection> countActiveByClassIds(@Param("classIds") List<Long> classIds);
    @Query("""
    SELECT COUNT(ce) 
    FROM ClassEnrollment ce 
    WHERE ce.classes.classId = :classId 
      AND ce.enrolledAt <= :monthEnd 
      AND (ce.unEnrolledAt IS NULL OR ce.unEnrolledAt >= :monthStart)
""")
    Long countActiveStudentsInMonth(
            @Param("classId") Long classId,
            @Param("monthStart") LocalDateTime monthStart,
            @Param("monthEnd") LocalDateTime monthEnd
    );
}
