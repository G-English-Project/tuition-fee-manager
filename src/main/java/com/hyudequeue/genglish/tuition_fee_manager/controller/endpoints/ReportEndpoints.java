package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;

public class ReportEndpoints {

    public static final String REPORT_API = "/api/reports";

    public static final String CREATE_REPORT = "/create";
    public static final String UPDATE_REPORT = "/update/{reportId}";
    public static final String DELETE_REPORT = "/delete/{reportId}";
    public static final String GET_REPORT_BY_ID = "/{reportId}";

    public static final String LIST_REPORTS_BY_STUDENT = "/student/{studentId}";
    public static final String LIST_REPORTS_BY_CLASS = "/class/{classId}";

    public static final String LIST_REPORTS_BY_STUDENT_IN_RANGE = "/student/{studentId}/range";
    public static final String LIST_REPORTS_BY_CLASS_IN_RANGE = "/class/{classId}/range";

    public static final String GET_REPORT_IMAGE_THUMB = "/{reportId}/thumb";
    public static final String GET_REPORT_IMAGE_FULL = "/{reportId}/full";
}
