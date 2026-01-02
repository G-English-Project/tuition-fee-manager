package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // ===== EXISTING =====
    @Query("SELECT f FROM Feedback f LEFT JOIN FETCH f.classes WHERE f.student.userId = :studentId")
    List<Feedback> findByStudentUserId(Long studentId);

    @Query("SELECT f FROM Feedback f LEFT JOIN FETCH f.classes WHERE f.teacher.userId = :teacherId")
    List<Feedback> findByTeacherUserId(Long teacherId);

    @Query("""
        SELECT f FROM Feedback f 
        LEFT JOIN FETCH f.classes 
        WHERE f.overallExperience BETWEEN 4 AND 5
        AND f.supportLevel = 'ALWAYS'
        ORDER BY f.createdAt DESC
    """)
    Page<Feedback> findGoodFeedbacks(Pageable pageable);

    // ===== SEARCH TOOL =====
    @Query("""
        SELECT f FROM Feedback f
        LEFT JOIN FETCH f.classes c
        WHERE (:teacherId IS NULL OR f.teacher.userId = :teacherId)
        AND (:classId IS NULL OR c.classId = :classId)
        AND (:overallRate IS NULL OR f.overallExperience = :overallRate)
        AND (:fromDate IS NULL OR f.createdAt >= :fromDate)
        AND (:toDate IS NULL OR f.createdAt <= :toDate)
        ORDER BY f.createdAt DESC
    """)
    Page<Feedback> searchFeedbacks(
            Long teacherId,
            Long classId,
            Integer overallRate,
            java.time.LocalDateTime fromDate,
            java.time.LocalDateTime toDate,
            Pageable pageable
    );
}
