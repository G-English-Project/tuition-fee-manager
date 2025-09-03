package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
