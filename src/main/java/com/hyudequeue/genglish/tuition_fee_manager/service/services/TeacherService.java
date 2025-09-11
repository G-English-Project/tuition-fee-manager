package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Teacher.request.TeacherRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Teacher.response.TeacherResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {

    // Create new teacher
    TeacherResponseDto createTeacher(TeacherRequestDto request);

    // Get teacher by ID
    TeacherResponseDto getTeacherById(Long id);

    // Get teacher by User ID
    TeacherResponseDto getTeacherByUserId(Long userId);

    // Get all teachers with pagination
    Page<TeacherResponseDto> getAllTeachers(Pageable pageable);

    // Update teacher
    TeacherResponseDto updateTeacher(Long id, TeacherRequestDto request);

    // Delete teacher
    void deleteTeacher(Long id);

    // Search teachers by name
    Page<TeacherResponseDto> searchTeachersByName(String name, Pageable pageable);

    // Get teachers by specialty
    List<TeacherResponseDto> getTeachersBySpecialty(String specialty);

    // Get teachers by language
    List<TeacherResponseDto> getTeachersByLanguage(String language);

    // Get teachers with minimum rating
    List<TeacherResponseDto> getTeachersWithMinRating(Double minRating);

    // Get top rated teachers
    List<TeacherResponseDto> getTopRatedTeachers();

    // Update teacher rating
    TeacherResponseDto updateTeacherRating(Long id, Double rating);
}
