package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Teacher.request.TeacherRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Teacher.response.TeacherResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Teacher;
import com.hyudequeue.genglish.tuition_fee_manager.repository.TeacherRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    @Override
    public TeacherResponseDto createTeacher(TeacherRequestDto request) {
        // Validate user exists
        if (!userRepository.existsById(request.getUserId())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "User not found with id: " + request.getUserId());
        }

        // Check if teacher already exists for this user
        if (teacherRepository.existsByUserId(request.getUserId())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Teacher already exists for user id: " + request.getUserId());
        }

        Teacher teacher = Teacher.builder()
                .name(request.getName())
                .userId(request.getUserId())
                .address(request.getAddress())
                .image(request.getImage())
                .rating(request.getRating() != null ? request.getRating() : 0.0)
                .experience(request.getExperience())
                .specialties(request.getSpecialties())
                .credentials(request.getCredentials())
                .languages(request.getLanguages())
                .bio(request.getBio())
                .achievements(request.getAchievements())
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);
        return TeacherResponseDto.toDto(savedTeacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Teacher not found with id: " + id));
        return TeacherResponseDto.toDto(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDto getTeacherByUserId(Long userId) {
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Teacher not found for user id: " + userId));
        return TeacherResponseDto.toDto(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherResponseDto> getAllTeachers(Pageable pageable) {
        Page<Teacher> teachers = teacherRepository.findAll(pageable);
        return teachers.map(TeacherResponseDto::toDto);
    }

    @Override
    public TeacherResponseDto updateTeacher(Long id, TeacherRequestDto request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Teacher not found with id: " + id));

        // Check if user exists if userId is being updated
        if (request.getUserId() != null && !request.getUserId().equals(teacher.getUserId())) {
            if (!userRepository.existsById(request.getUserId())) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(404), "User not found with id: " + request.getUserId());
            }
            // Check if another teacher already exists for this user
            if (teacherRepository.existsByUserId(request.getUserId())) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Teacher already exists for user id: " + request.getUserId());
            }
        }

        // Update fields
        if (request.getName() != null) teacher.setName(request.getName());
        if (request.getUserId() != null) teacher.setUserId(request.getUserId());
        if (request.getAddress() != null) teacher.setAddress(request.getAddress());
        if (request.getImage() != null) teacher.setImage(request.getImage());
        if (request.getRating() != null) teacher.setRating(request.getRating());
        if (request.getExperience() != null) teacher.setExperience(request.getExperience());
        if (request.getSpecialties() != null) teacher.setSpecialties(request.getSpecialties());
        if (request.getCredentials() != null) teacher.setCredentials(request.getCredentials());
        if (request.getLanguages() != null) teacher.setLanguages(request.getLanguages());
        if (request.getBio() != null) teacher.setBio(request.getBio());
        if (request.getAchievements() != null) teacher.setAchievements(request.getAchievements());

        Teacher updatedTeacher = teacherRepository.save(teacher);
        return TeacherResponseDto.toDto(updatedTeacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeacherResponseDto> searchTeachersByName(String name, Pageable pageable) {
        Page<Teacher> teachers = teacherRepository.findByNameContainingIgnoreCase(name, pageable);
        return teachers.map(TeacherResponseDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getTeachersBySpecialty(String specialty) {
        List<Teacher> teachers = teacherRepository.findBySpecialty(specialty);
        return teachers.stream()
                .map(TeacherResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getTeachersByLanguage(String language) {
        List<Teacher> teachers = teacherRepository.findByLanguage(language);
        return teachers.stream()
                .map(TeacherResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getTeachersWithMinRating(Double minRating) {
        List<Teacher> teachers = teacherRepository.findByRatingGreaterThanEqual(minRating);
        return teachers.stream()
                .map(TeacherResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDto> getTopRatedTeachers() {
        List<Teacher> teachers = teacherRepository.findTop10ByOrderByRatingDesc();
        return teachers.stream()
                .map(TeacherResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TeacherResponseDto updateTeacherRating(Long id, Double rating) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Teacher not found with id: " + id));
        
        teacher.setRating(rating);
        Teacher updatedTeacher = teacherRepository.save(teacher);
        return TeacherResponseDto.toDto(updatedTeacher);
    }
}
