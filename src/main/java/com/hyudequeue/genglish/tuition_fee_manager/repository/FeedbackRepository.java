package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStudentUserId(Long studentId);
    List<Feedback> findByTeacherUserId(Long teacherId);
}