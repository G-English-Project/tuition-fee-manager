package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findByStudent_UserId(Long studentId, Pageable pageable);

    Page<Report> findByClassRoom_ClassId(Long classId, Pageable pageable);

    Page<Report> findByStudent_UserIdAndCreatedAtBetween(Long studentId,
                                                         LocalDateTime from,
                                                         LocalDateTime to,
                                                         Pageable pageable);

    Page<Report> findByClassRoom_ClassIdAndCreatedAtBetween(Long classId,
                                                            LocalDateTime from,
                                                            LocalDateTime to,
                                                            Pageable pageable);

    @Query("""
        SELECT r FROM Report r
        WHERE (:classId IS NULL OR r.classRoom.classId = :classId)
          AND (:studentId IS NULL OR r.student.userId = :studentId)
          AND (:teacherId IS NULL OR r.teacher.userId = :teacherId)
          AND (:from IS NULL OR r.createdAt >= :from)
          AND (:to IS NULL OR r.createdAt <= :to)
        ORDER BY r.createdAt DESC
        """)
    Page<Report> findAllReportsWithFilters(@Param("classId") Long classId,
                                           @Param("studentId") Long studentId,
                                           @Param("teacherId") Long teacherId,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to,
                                           Pageable pageable);
}
