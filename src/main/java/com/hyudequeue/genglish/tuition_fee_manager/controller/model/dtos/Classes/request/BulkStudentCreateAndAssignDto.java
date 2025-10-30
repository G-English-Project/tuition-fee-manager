package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkStudentCreateAndAssignDto {
    private Long classId;
    private List<StudentData> students;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentData {
        private String email;
        private String fullName;
        private String phone;
        private LocalDate dateOfBirth;
    }
}