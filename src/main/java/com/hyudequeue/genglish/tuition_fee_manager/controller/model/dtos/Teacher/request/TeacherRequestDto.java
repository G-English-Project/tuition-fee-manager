package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Teacher.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherRequestDto {
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
}
