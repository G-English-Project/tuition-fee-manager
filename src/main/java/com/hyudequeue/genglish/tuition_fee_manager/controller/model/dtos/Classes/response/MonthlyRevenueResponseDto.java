package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueResponseDto {
    private Long classId;
    private String className;
    private BigDecimal currentClassFee;
    private List<MonthlyRevenueDetail> monthlyRevenues;
    private BigDecimal totalRevenue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenueDetail {
        private Integer year;
        private Integer month;
        private String monthName; // e.g., "2024-01"
        private Long activeStudentCount;
        private BigDecimal revenue;
    }
}