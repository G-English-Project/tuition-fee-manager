package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Classes, Long> {
    @Query("""
        SELECT u.enrolledClass.classId, COUNT(u)
        FROM User u
        WHERE u.enrolledClass.classId IN :classIds
          AND u.unenrolled IS NULL
          AND u.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum.ACTIVE
        GROUP BY u.enrolledClass.classId
    """)
    List<Object[]> countActiveByClassIds(@Param("classIds") List<Long> classIds);
}
