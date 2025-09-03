package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request;

import lombok.Data;

@Data
public class UpdateReportRequest {
    private String title;
    private String content;

    private String imageThumbBase64;
    private String imageBase64;
    private String mimeType;
}