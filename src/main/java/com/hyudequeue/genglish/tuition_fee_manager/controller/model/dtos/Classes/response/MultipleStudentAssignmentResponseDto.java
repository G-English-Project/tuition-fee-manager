package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Enrollment.response.EnrollmentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultipleStudentAssignmentResponseDto {
    private List<EnrollmentResponseDto> successfulEnrollments;
    private List<FailedAssignmentDto> failedAssignments;
    private int totalProcessed;
    private int successCount;
    private int failedCount;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailedAssignmentDto {
        private Long studentId;
        private String reason;
        private String studentName;
    }
}