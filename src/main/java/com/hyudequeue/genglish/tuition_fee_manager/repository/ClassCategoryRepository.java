package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassCategoryRepository extends JpaRepository<ClassCategory, Long> {
}