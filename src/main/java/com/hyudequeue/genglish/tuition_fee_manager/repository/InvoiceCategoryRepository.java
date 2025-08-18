package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.CategoryStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.InvoiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceCategoryRepository extends JpaRepository<InvoiceCategory, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<InvoiceCategory> findByNameIgnoreCase(String name);
    Page<InvoiceCategory> findAllByStatus(CategoryStatusEnum status, Pageable pageable);
    boolean existsByName(String name);
}