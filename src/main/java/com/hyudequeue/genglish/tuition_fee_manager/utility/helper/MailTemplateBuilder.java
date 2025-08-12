package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class MailTemplateBuilder {

    public static String buildHtml(NotificationTemplateEnum template, Map<String, String> values) {
        String subject = applyValues(getSubjectTemplate(template), values);
        String body = applyValues(getBodyTemplate(template), values);
        return wrapInHtml(subject, body);
    }

    private static String applyValues(String template, Map<String, String> values) {
        if (template == null || values == null || values.isEmpty()) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return result;
    }

    private static String getSubjectTemplate(NotificationTemplateEnum type) {
        return switch (type) {
            case STUDENT_PAID_INVOICE -> "Học sinh {studentName} đã thanh toán";
            case CLASS_INVOICE_CREATED -> "Tạo đơn học phí mới cho lớp {className}";
            case STUDENT_INVOICE_CREATED -> "Tạo hóa đơn cho học sinh {studentName}";
            case OVERDUE_INVOICE_ALERT -> "Cảnh báo: Có hóa đơn quá hạn";
            case STUDENT_REMOVED_FROM_CLASS -> "Học sinh bị xóa khỏi lớp {className}";
            case CLASS_TUITION_UPDATED -> "Đã cập nhật học phí lớp {className}";
            case INVOICE_CANCELLED_ALERT -> "Hóa đơn bị hủy: {invoiceContent}";
            case NEW_INVOICE_NOTIFICATION -> "Bạn có hóa đơn mới: {invoiceContent}";
            case STUDENT_SUCCESSFUL_PAYMENT -> "Đã thanh toán: {invoiceContent}";
            case STUDENT_INVOICE_OVERDUE -> "Hóa đơn quá hạn: {invoiceContent}";
            case STUDENT_ADDED_TO_CLASS -> "Bạn được thêm vào lớp {className}";
            case STUDENT_REMOVED_FROM_CLASS_STUDENT -> "Bạn bị xóa khỏi lớp {className}";
            case STUDENT_TUITION_EDITED -> "Đơn học phí đã được chỉnh sửa";
            default -> "Thông báo hệ thống";
        };
    }

    private static String getBodyTemplate(NotificationTemplateEnum type) {
        return switch (type) {
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
            default -> "[Không tìm thấy nội dung thông báo]";
        };
    }

    private static String wrapInHtml(String subject, String body) {
        try {
            // Load the HTML template from resources
            String htmlTemplate = Files.readString(Paths.get("src/main/resources/templates/mail-template.html"));
            return String.format(htmlTemplate, subject, body);
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file mail-template.html", e);
        }
    }
}
