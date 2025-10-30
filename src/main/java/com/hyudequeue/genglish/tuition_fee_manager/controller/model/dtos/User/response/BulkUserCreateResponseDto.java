package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUserCreateResponseDto {
    private int totalRequested;
    private int successfullyCreated;
    private int failed;
    private List<UserCreateResult> results;
    private List<String> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserCreateResult {
        private String email;
        private String fullName;
        private Long userId;
        private boolean success;
        private String error;
        private String defaultPassword;
    }
}