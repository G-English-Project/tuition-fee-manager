package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {

}
