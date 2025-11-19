package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;


public class FeedbackEndpoints {
    public static final String BASE = "/api/v1/feedbacks";

    public static final String CREATE_FEEDBACK = BASE;
    public static final String GET_FEEDBACK_BY_ID = BASE + "/{feedbackId}";
    public static final String GET_ALL_FEEDBACKS = BASE;
    public static final String UPDATE_FEEDBACK = BASE + "/{feedbackId}";
    public static final String DELETE_FEEDBACK = BASE + "/{feedbackId}";

    public static final String GET_FEEDBACKS_BY_STUDENT = BASE + "/student/{studentId}";
    public static final String GET_FEEDBACKS_BY_TEACHER = BASE + "/teacher/{teacherId}";
    public static final String GET_GOOD_FEEDBACKS = "/good-feedbacks";

    private FeedbackEndpoints() {
        // Prevent instantiation
    }
}
