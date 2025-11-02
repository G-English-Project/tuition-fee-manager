package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ClassCategory;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.CategoryStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassCategoryRepository extends JpaRepository<ClassCategory, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<ClassCategory> findByNameIgnoreCase(String name);
    List<ClassCategory> findAllByStatus(CategoryStatusEnum status);
}