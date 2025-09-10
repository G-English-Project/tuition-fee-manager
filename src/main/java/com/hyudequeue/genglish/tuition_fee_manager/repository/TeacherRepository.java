package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    // Tìm teacher theo userId
    Optional<Teacher> findByUserId(Long userId);

    // Tìm tất cả teachers có rating >= minRating
    List<Teacher> findByRatingGreaterThanEqual(Double minRating);

    // Tìm teachers theo specialty
    @Query("SELECT t FROM Teacher t JOIN t.specialties s WHERE s = :specialty")
    List<Teacher> findBySpecialty(@Param("specialty") String specialty);

    // Tìm teachers theo tên (case insensitive)
    Page<Teacher> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm teachers theo ngôn ngữ
    @Query("SELECT t FROM Teacher t JOIN t.languages l WHERE l = :language")
    List<Teacher> findByLanguage(@Param("language") String language);

    // Tìm top teachers theo rating
    List<Teacher> findTop10ByOrderByRatingDesc();

    // Kiểm tra userId đã tồn tại chưa
    boolean existsByUserId(Long userId);
}
