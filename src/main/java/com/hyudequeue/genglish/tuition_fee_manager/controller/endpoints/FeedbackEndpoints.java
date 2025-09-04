package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;

public final class FeedbackEndpoints {
    private FeedbackEndpoints() {}

    public static final String BASE = "";

    public static final String CREATE_FEEDBACK = "/create"; // Học sinh tạo feedback
    public static final String GET_ALL_FEEDBACKS = "/all";  // Admin xem tất cả feedback
    public static final String GET_FEEDBACKS_BY_STUDENT = "/student/{studentId}"; // Feedback theo học sinh
    public static final String UPDATE_FEEDBACK = "/{feedbackId}/update";
    public static final String DELETE_FEEDBACK = "/{feedbackId}/delete";
}
