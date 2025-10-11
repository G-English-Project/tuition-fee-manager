package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;

import java.util.Map;

public class NotificationTemplateBuilder {

    public static String buildSubject(NotificationTemplateEnum template, Map<String, String> values) {
        String templateStr = getSubjectTemplate(template);
        return applyValues(templateStr, values);
    }

    public static String buildBody(NotificationTemplateEnum template, Map<String, String> values) {
        String templateStr = getBodyTemplate(template);
        return applyValues(templateStr, values);
    }

    private static String applyValues(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    private static String getSubjectTemplate(NotificationTemplateEnum type) {
        return switch (type) {
            // --- THẦY/CÔ ---
            case STUDENT_PAID_INVOICE -> "Học sinh {studentName} đã thanh toán";
            case CLASS_INVOICE_CREATED -> "Tạo đơn học phí mới cho lớp {className}";
            case STUDENT_INVOICE_CREATED -> "Tạo hóa đơn cho học sinh {studentName}";
            case OVERDUE_INVOICE_ALERT -> "Cảnh báo: Có hóa đơn quá hạn";

            case STUDENT_REMOVED_FROM_CLASS -> "Học sinh bị xóa khỏi lớp {className}";
            case CLASS_TUITION_UPDATED -> "Đã cập nhật học phí lớp {className}";
            case INVOICE_CANCELLED_ALERT -> "Hóa đơn bị hủy: {invoiceContent}";

            case NEW_FEEDBACK_RECEIVED -> "Feedback mới từ {studentName}";
            // --- HỌC SINH ---
            case NEW_INVOICE_NOTIFICATION -> "Bạn có hóa đơn mới: {invoiceContent}";
            case STUDENT_SUCCESSFUL_PAYMENT -> "Đã thanh toán: {invoiceContent}";
            case STUDENT_INVOICE_OVERDUE -> "Hóa đơn quá hạn: {invoiceContent}";

            case STUDENT_ADDED_TO_CLASS -> "Bạn được thêm vào lớp {className}";
            case STUDENT_REMOVED_FROM_CLASS_STUDENT -> "Bạn bị xóa khỏi lớp {className}";
            case STUDENT_TUITION_EDITED -> "Đơn học phí đã được chỉnh sửa";
            // --- NHẮC NHỞ ---
            case STUDENT_OVERDUE_REMINDER -> "Nhắc nhở học phí còn nợ sau 10 ngày";
            case STUDENT_MANUAL_REMINDER -> "Nhắc học phí chưa thanh toán";
            default -> "Thông báo hệ thống";
        };
    }

    private static String getBodyTemplate(NotificationTemplateEnum type) {
        return switch (type) {
            // --- THẦY/CÔ ---
            case STUDENT_PAID_INVOICE ->
                    "Học sinh {studentName} đã thanh toán hóa đơn với nội dung: {invoiceContent}.";
            case CLASS_INVOICE_CREATED ->
                    "Đã tạo thành công đơn học phí cho lớp {className}.";
            case STUDENT_INVOICE_CREATED ->
                    "Đã tạo thành công hóa đơn cho học sinh {studentName}.";
            case OVERDUE_INVOICE_ALERT ->
                    "Có hóa đơn thanh toán bị quá hạn. Vui lòng kiểm tra.";

            case STUDENT_REMOVED_FROM_CLASS ->
                    "Học sinh {studentName} đã bị xóa khỏi lớp {className}.";
            case CLASS_TUITION_UPDATED ->
                    "Đã cập nhật học phí cho lớp {className} – tổng số học sinh: {studentCount}.";
            case INVOICE_CANCELLED_ALERT ->
                    "Hóa đơn với nội dung {invoiceContent} đã bị hủy. Vui lòng kiểm tra.";
            case NEW_FEEDBACK_RECEIVED ->
                    "Feedback mới từ {studentName}";
            // --- HỌC SINH ---
            case NEW_INVOICE_NOTIFICATION ->
                    "Bạn có hóa đơn thanh toán mới với nội dung {invoiceContent}, vui lòng kiểm tra.";
            case STUDENT_SUCCESSFUL_PAYMENT ->
                    "Đã thanh toán thành công cho hóa đơn với nội dung {invoiceContent}.";
            case STUDENT_INVOICE_OVERDUE ->
                    "Hóa đơn với nội dung {invoiceContent} đã quá hạn, vui lòng thanh toán sớm.";

            case STUDENT_ADDED_TO_CLASS ->
                    "Bạn được thêm vào lớp {className}.";
            case STUDENT_REMOVED_FROM_CLASS_STUDENT ->
                    "Bạn đã bị xóa khỏi lớp {className}.";
            case STUDENT_TUITION_EDITED ->
                    "Đơn học phí của bạn đã được chỉnh sửa – vui lòng kiểm tra lại chi tiết.";
            // --- NHẮC NHỞ ---
            case STUDENT_OVERDUE_REMINDER ->
                    "Chào {studentName}, hóa đơn '{invoiceContent}' của bạn đã quá hạn {daysOverdue} ngày. "
                            + "Vui lòng thanh toán sớm để tránh gián đoạn việc học.";
            case STUDENT_MANUAL_REMINDER ->
                    "Chào {studentName}, bạn vui lòng kiểm tra và hoàn tất thanh toán cho hóa đơn '{invoiceContent}' trong thời gian sớm nhất.";
            default -> "[Không tìm thấy nội dung thông báo]";
        };
    }
}
