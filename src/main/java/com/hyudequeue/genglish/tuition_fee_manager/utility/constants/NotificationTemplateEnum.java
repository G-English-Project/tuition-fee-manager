package com.hyudequeue.genglish.tuition_fee_manager.utility.constants;

/**
 * Danh sách các template thông báo được chuẩn hóa theo từng tình huống sử dụng.
 * Dùng trong NotificationService để sinh subject và body từ enum + biến.
 */
public enum NotificationTemplateEnum {

    // =============================
    // ========== THẦY/CÔ ==========
    // =============================

    /**
     * Học sinh A đã thanh toán hóa đơn X
     */
    STUDENT_PAID_INVOICE,

    /**
     * Đã tạo đơn học phí cho lớp
     */
    CLASS_INVOICE_CREATED,

    /**
     * Đã tạo hóa đơn cho học sinh
     */
    STUDENT_INVOICE_CREATED,

    /**
     * Có hóa đơn bị quá hạn
     */
    OVERDUE_INVOICE_ALERT,

    /**
     * Học sinh bị xóa khỏi lớp
     */
    STUDENT_REMOVED_FROM_CLASS,

    /**
     * Đã cập nhật học phí cho lớp
     */
    CLASS_TUITION_UPDATED,

    /**
     * Hóa đơn đã bị hủy
     */
    INVOICE_CANCELLED_ALERT,

    // =============================
    // ========== HỌC SINH =========
    // =============================

    /**
     * Bạn có hóa đơn mới
     */
    NEW_INVOICE_NOTIFICATION,

    /**
     * Bạn đã thanh toán thành công
     */
    STUDENT_SUCCESSFUL_PAYMENT,

    /**
     * Hóa đơn quá hạn
     */
    STUDENT_INVOICE_OVERDUE,

    /**
     * Bạn được thêm vào lớp
     */
    STUDENT_ADDED_TO_CLASS,

    /**
     * Bạn bị xóa khỏi lớp
     */
    STUDENT_REMOVED_FROM_CLASS_STUDENT,

    /**
     * Đơn học phí đã được chỉnh sửa
     */
    STUDENT_TUITION_EDITED,

    NEW_FEEDBACK_RECEIVED,
    STUDENT_OVERDUE_REMINDER,   // Nhắc tự động sau mỗi 10 ngày
    STUDENT_MANUAL_REMINDER   // Nhắc thủ công do admin gửi

}
