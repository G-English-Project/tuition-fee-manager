package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceStatResponseDto {
    private long unpaidCount;
    private int unpaidTotal;
    private long overdueCount;
    private int overdueTotal;
    private int inactivestudentcount;
    private int currentMonthTotal;
}