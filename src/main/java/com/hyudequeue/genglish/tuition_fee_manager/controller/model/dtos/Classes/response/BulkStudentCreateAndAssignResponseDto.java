package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkStudentCreateAndAssignResponseDto {
    private List<SuccessfulStudent> successfulStudents;
    private List<FailedStudent> failedStudents;
    private Integer totalProcessed;
    private Integer successCount;
    private Integer failedCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SuccessfulStudent {
        private Long userId;
        private String email;
        private String fullName;
        private Long enrollmentId;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailedStudent {
        private String email;
        private String fullName;
        private String reason;
    }
}