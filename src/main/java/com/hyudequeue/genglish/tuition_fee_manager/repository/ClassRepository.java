package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Classes, Long> {
    Page<Classes> findByStatus(ClassStatusEnum status, Pageable pageable);
    List<Classes> findByTeacherId(Long teacherId);
    List<Classes> findByClassCategory_CategoryId(Long categoryId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Classes c
           SET c.status   = :inactive,
               c.updatedAt = CURRENT_TIMESTAMP
         WHERE c.effectiveTo IS NOT NULL
           AND c.effectiveTo < :today
           AND c.status <> :inactive
    """)
    int deactivateExpired(@Param("today") LocalDate today,
                          @Param("inactive") ClassStatusEnum inactive);
}
