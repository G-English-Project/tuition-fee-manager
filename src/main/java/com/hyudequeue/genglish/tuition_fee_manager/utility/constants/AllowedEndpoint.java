package com.hyudequeue.genglish.tuition_fee_manager.utility.constants;

public final class AllowedEndpoint {

    // 🟢 Public (không cần token)
    public static final String[] GENERAL = {
            ApiPathConstants.AUTH_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.AuthEndpoints.LOGIN_ENDPOINT,
            ApiPathConstants.PAYMENT_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.PaymentEndpoints.WEBHOOK
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
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.MARK_AS_READ,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.MARK_MULTIPLE_AS_READ,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.MARK_ALL_AS_READ,
            ApiPathConstants.NOTIFICATION_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.COUNT_UNREAD,

            // User profile
            ApiPathConstants.USER_API + com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.UserEndpoints.PROFILE_ENDPOINT
    };

    private AllowedEndpoint() {
        // prevent instantiation
    }
}
