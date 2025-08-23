package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RevenueSummaryDto {
    private String group;
    private BigDecimal total;

    public RevenueSummaryDto(String group, BigDecimal total) {
        this.group = group;
        this.total = total;
    }
    public RevenueSummaryDto(String group, Long total) {
        this.group = group;
        this.total = (total == null) ? null : BigDecimal.valueOf(total);
    }
    public RevenueSummaryDto(String group, Integer total) {
        this.group = group;
        this.total = (total == null) ? null : BigDecimal.valueOf(total.longValue());
    }
    public RevenueSummaryDto(String group, Double total) {
        this.group = group;
        this.total = (total == null) ? null : BigDecimal.valueOf(total);
    }
}
