package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueSummaryDto {
    private String group;
    private Long total;
}