package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;

public final class ReviewEndpoints {
    private ReviewEndpoints() {}

    public static final String BASE = "";

    public static final String CREATE_REVIEW = "/create"; // Học sinh tạo feedback
    public static final String GET_ALL_REVIEWS = "/all";  // Admin xem tất cả feedback
    public static final String GET_REVIEWS_BY_STUDENT = "/student/{studentId}"; // Feedback theo học sinh
    public static final String UPDATE_REVIEW = "/{reviewId}/update";
    public static final String DELETE_REVIEW = "/{reviewId}/delete";
}
