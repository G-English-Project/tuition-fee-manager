package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserWithClassDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Page<User> findByRole(RoleEnum role, Pageable pageable);
    boolean existsByEmail(String email);
    @Query("""
    SELECT u FROM User u
    JOIN ClassEnrollment ce ON ce.user = u
    WHERE ce.classes = :classes
      AND ce.unEnrolledAt IS NULL
    """)
    List<User> findAllByEnrolledClass(@Param("classes") Classes classes);

    @Query("""
    SELECT new com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserWithClassDto(
        u.userId,
        u.email,
        u.fullName,
        c.classId,
        c.className,
        u.createdAt
    )
    FROM User u
    LEFT JOIN ClassEnrollment ce ON ce.user = u AND ce.unEnrolledAt IS NULL
    LEFT JOIN Classes c ON ce.classes = c
    WHERE u.role = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum.STUDENT
      AND u.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum.ACTIVE
    ORDER BY u.createdAt DESC
""")
    Page<UserWithClassDto> findAllActiveStudentsWithCurrentClass(Pageable pageable);

    @Query("""
    SELECT new com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserWithClassDto(
        u.userId,
        u.email,
        u.fullName,
        c.classId,
        c.className,
        u.createdAt
    )
    FROM User u
    LEFT JOIN ClassEnrollment ce ON ce.user = u AND ce.unEnrolledAt IS NULL
    LEFT JOIN Classes c ON ce.classes = c
    WHERE u.role = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum.STUDENT
      AND u.status = com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.UserStatusEnum.ACTIVE
      AND (
            LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(c.className) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
        ORDER BY u.createdAt DESC
    """)
    Page<UserWithClassDto> searchStudentsWithClassByKeyword(@Param("keyword") String keyword, Pageable pageable);


}
