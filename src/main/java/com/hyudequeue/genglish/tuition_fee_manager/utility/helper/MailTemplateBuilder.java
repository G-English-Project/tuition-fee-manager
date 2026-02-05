package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MailTemplateBuilder {

    public static String buildHtml(NotificationTemplateEnum template, Map<String, String> values) {
        String subject = applyValues(getSubjectTemplate(template), values);
        String body = applyValues(getBodyTemplate(template), values);

        try {
            return wrapInHtml(subject, body);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load mail template", e);
        }
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
            case STUDENT_PAID_INVOICE -> "Biên lai thu tiền – Mã hóa đơn #{invoiceId}";
            case CLASS_INVOICE_CREATED -> "Tạo đơn học phí mới cho lớp {className}";
            case STUDENT_INVOICE_CREATED -> "Tạo hóa đơn cho học sinh {studentName}";
            case OVERDUE_INVOICE_ALERT -> "Cảnh báo: Có hóa đơn quá hạn";
            case STUDENT_REMOVED_FROM_CLASS -> "Học sinh bị xóa khỏi lớp {className}";
            case CLASS_TUITION_UPDATED -> "Đã cập nhật học phí lớp {className}";
            case INVOICE_CANCELLED_ALERT -> "Hóa đơn bị hủy: {invoiceContent}";
            case NEW_INVOICE_NOTIFICATION -> "Bạn có hóa đơn mới: {invoiceContent}";
            case STUDENT_SUCCESSFUL_PAYMENT -> "Học sinh {studentName} đã thanh toán hóa đơn với nội dung: {invoiceContent}";
            case STUDENT_INVOICE_OVERDUE -> "Hóa đơn quá hạn: {invoiceContent}";
            case STUDENT_ADDED_TO_CLASS -> "Bạn được thêm vào lớp {className}";
            case STUDENT_REMOVED_FROM_CLASS_STUDENT -> "Bạn bị xóa khỏi lớp {className}";
            case STUDENT_TUITION_EDITED -> "Đơn học phí đã được chỉnh sửa";
            // --- NHẮC NHỞ ---
            case STUDENT_OVERDUE_REMINDER -> "Nhắc nhở học phí còn nợ sau 10 ngày";
            case STUDENT_MANUAL_REMINDER -> "Nhắc học phí chưa thanh toán";
            case FEEDBACK_REMINDER -> "Nhắc nhở điền feedback định kỳ";

            default -> "Thông báo hệ thống";
        };
    }

    private static String getBodyTemplate(NotificationTemplateEnum type) {
        return switch (type) {
            case STUDENT_PAID_INVOICE ->
                    "<h3>BIÊN LAI THU TIỀN (RECEIPT)</h3>"
                            + "<ul>"
                            + "<li><b>Tên học viên:</b> {studentName}</li>"
                            + "<li><b>Lớp:</b> {className}</li>"
                            + "<li><b>Mã hóa đơn:</b> {invoiceId}</li>"
                            + "<li><b>Nội dung thu phí:</b> {invoiceContent}</li>"
                            + "<li><b>Số tiền đã thanh toán:</b> {amount} VND</li>"
                            + "<li><b>Thời gian thanh toán:</b> {paidAt}</li>"
                            + "</ul>"
                            + "<p>Email này là biên lai xác nhận thanh toán. "
                            + "Vui lòng lưu lại để đối chiếu khi cần.</p>";
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
                    "<h3>BIÊN LAI THU TIỀN (RECEIPT)</h3>"
                            + "<p>Thông tin thanh toán:</p>"
                            + "<ul>"
                            + "<li><b>Tên học viên:</b> {studentName}</li>"
                            + "<li><b>Mã hóa đơn:</b> {invoiceId}</li>"
                            + "<li><b>Nội dung thu phí:</b> {invoiceContent}</li>"
                            + "<li><b>Số tiền đã thanh toán:</b> {amount} VND</li>"
                            + "</ul>"
                            + "<p>Email này là biên lai xác nhận thanh toán. "
                            + "Vui lòng lưu lại để đối chiếu khi cần.</p>";
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
            case FEEDBACK_REMINDER ->
                    "Chào {studentName},<br/><br/>"
                            + "Đã 2 tháng kể từ lần góp ý gần nhất. Trung tâm rất mong nhận được chia sẻ của bạn.<br/>"
                            + "Vui lòng điền form tại đây:<br/><b>{feedbackLink}</b><br/><br/>"
                            + "Xin cảm ơn bạn!";

            default -> "[Không tìm thấy nội dung thông báo]";
        };
    }

    private static String wrapInHtml(String subject, String body) throws IOException {
        try (InputStream inputStream = MailTemplateBuilder.class.getResourceAsStream("/MailTemplate/mail-template.html")) {
            if (inputStream == null) {
                throw new FileNotFoundException("mail-template.html not found in resources/MailTemplate");
            }
            String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return String.format(htmlTemplate, subject, body);
        }
    }
}
