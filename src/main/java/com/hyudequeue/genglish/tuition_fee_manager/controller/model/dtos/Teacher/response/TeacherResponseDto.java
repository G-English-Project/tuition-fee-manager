package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Teacher.response;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherResponseDto {
    private Long id;
    private String name;
    private Long userId;
    private String address;
    private String image;
    private Double rating;
    private String experience;
    private List<String> specialties;
    private List<String> credentials;
    private List<String> languages;
    private String bio;
    private List<String> achievements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TeacherResponseDto toDto(Teacher teacher) {
        return TeacherResponseDto.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .userId(teacher.getUserId())
                .address(teacher.getAddress())
                .image(teacher.getImage())
                .rating(teacher.getRating())
                .experience(teacher.getExperience())
                .specialties(teacher.getSpecialties())
                .credentials(teacher.getCredentials())
                .languages(teacher.getLanguages())
                .bio(teacher.getBio())
                .achievements(teacher.getAchievements())
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
    }
}
