package com.hyudequeue.genglish.tuition_fee_manager.utility.constants;

import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.FeedbackEndpoints;
import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.PaymentEndpoints;
import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ReportEndpoints;
import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ReviewEndpoints;
import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints;
import com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.UserEndpoints;

public final class AllowedEndpoint {

    // 🟢 Public (không cần token)
    public static final String[] GENERAL = {
            ApiPathConstants.AUTH_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.AuthEndpoints.LOGIN_ENDPOINT,
            ApiPathConstants.PAYMENT_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.PaymentEndpoints.WEBHOOK,
            ApiPathConstants.USER_API + UserEndpoints.CHANGE_PASSWORD_ENDPOINT,
            ApiPathConstants.PAYMENT_API + PaymentEndpoints.CREATE,
            ApiPathConstants.REVIEW_API + ReviewEndpoints.CREATE_REVIEW,
            ApiPathConstants.REVIEW_API + ReviewEndpoints.GET_ALL_REVIEWS,
            ApiPathConstants.FEEDBACK_API + FeedbackEndpoints.CREATE_FEEDBACK,

    };

    // 👨‍🎓 Student
    public static final String[] STUDENT = {
            // Class
            ApiPathConstants.CLASS_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ClassEndpoints.GET_ALL_CLASSES,
            ApiPathConstants.CLASS_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ClassEndpoints.GET_CLASS_BY_ID,
            ApiPathConstants.CLASS_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ClassEndpoints.ENROLLMENT_BY_STUDENT,

            // Invoice
            ApiPathConstants.INVOICE_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.InvoiceEndpoints.GET_BY_STUDENT,
            ApiPathConstants.INVOICE_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.InvoiceEndpoints.GET_BY_STATUS,

            // Payment
            ApiPathConstants.PAYMENT_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.PaymentEndpoints.GET_BY_ID,
            ApiPathConstants.PAYMENT_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.PaymentEndpoints.GET_LATEST_BY_INVOICE,

            // Notifications
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.GET_ALL_BY_USER,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.GET_ACTIVE_BY_USER,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.MARK_AS_READ,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.MARK_MULTIPLE_AS_READ,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.MARK_ALL_AS_READ,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.MARK_AS_DELETE,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.MARK_ALL_AS_DELETE,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.COUNT_UNREAD,

            // User profile
            ApiPathConstants.USER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.UserEndpoints.PROFILE_ENDPOINT,
            ApiPathConstants.USER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.UserEndpoints.EDIT_ENDPOINT,

            //Review
            ApiPathConstants.REVIEW_API + ReviewEndpoints.GET_REVIEWS_BY_STUDENT,

            // Feedback
            ApiPathConstants.FEEDBACK_API + FeedbackEndpoints.GET_FEEDBACKS_BY_STUDENT,

            // Report (allow students to view their reports)
            "/api/v1/reports/student/**",
            "/api/v1/reports/*/thumb",
            "/api/v1/reports/*/full",
            "/api/v1/reports/*",

            // Teacher (read-only for students)
            ApiPathConstants.TEACHER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints.GET_ALL,
            ApiPathConstants.TEACHER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints.GET_BY_ID,
            ApiPathConstants.TEACHER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints.SEARCH_BY_NAME,
            ApiPathConstants.TEACHER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints.GET_BY_SPECIALTY,
            ApiPathConstants.TEACHER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints.GET_BY_LANGUAGE,
            ApiPathConstants.TEACHER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints.GET_BY_MIN_RATING,
            ApiPathConstants.TEACHER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints.GET_TOP_RATED

    };

    private AllowedEndpoint() {
        // prevent instantiation
    }
}
