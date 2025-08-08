package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
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
        String htmlTemplate = """
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>G-English Notification</title>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                    padding: 20px;
                    margin: 0;
                    line-height: 1.6;
                }
                
                .email-wrapper {
                    max-width: 600px;
                    margin: 0 auto;
                    background: #ffffff;
                    border-radius: 16px;
                    overflow: hidden;
                    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
                }
                
                .email-header {
                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                    padding: 40px 30px;
                    text-align: center;
                    position: relative;
                }
                
                .email-header::before {
                    content: '';
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(#grain)"/></svg>');
                    opacity: 0.3;
                }
                
                .logo-container {
                    position: relative;
                    z-index: 1;
                    margin-bottom: 20px;
                }
                
                .logo {
                    width: 60px;
                    height: 60px;
                    background: rgba(255, 255, 255, 0.2);
                    border-radius: 50%%;
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: 15px;
                    backdrop-filter: blur(10px);
                    border: 2px solid rgba(255, 255, 255, 0.3);
                }
                
                .logo img {
                    width: 32px;
                    height: 32px;
                    display: block; /* removes inline image spacing */
                    margin: auto; /* ensures image stays centered even if flex fails */
                }
                
                .brand-name {
                    color: white;
                    font-size: 28px;
                    font-weight: 700;
                    margin: 0;
                    position: relative;
                    z-index: 1;
                    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
                }
                
                .brand-tagline {
                    color: rgba(255, 255, 255, 0.9);
                    font-size: 14px;
                    margin-top: 8px;
                    position: relative;
                    z-index: 1;
                }
                
                .email-content {
                    padding: 40px 30px;
                }
                
                .content-title {
                    color: #2d3748;
                    font-size: 24px;
                    font-weight: 600;
                    margin-bottom: 20px;
                    text-align: center;
                }
                
                .content-body {
                    color: #4a5568;
                    font-size: 16px;
                    line-height: 1.8;
                    margin-bottom: 30px;
                    text-align: left;
                }
                
                .cta-section {
                    text-align: center;
                    margin: 30px 0;
                }
                
                .cta-button {
                    display: inline-block;
                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                    color: #ffffff;
                    padding: 14px 28px;
                    text-decoration: none;
                    border-radius: 8px;
                    font-weight: 600;
                    font-size: 16px;
                    transition: transform 0.2s ease;
                    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                }
                
                .cta-button:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
                }
                
                .divider {
                    height: 1px;
                    background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
                    margin: 30px 0;
                }
                
                .email-footer {
                    background: #f7fafc;
                    padding: 30px;
                    text-align: center;
                    border-top: 1px solid #e2e8f0;
                }
                
                .footer-content {
                    color: #718096;
                    font-size: 14px;
                    line-height: 1.6;
                }
                
                .footer-links {
                    margin-top: 20px;
                }
                
                .footer-links a {
                    color: #667eea;
                    text-decoration: none;
                    margin: 0 15px;
                    font-weight: 500;
                }
                
                .footer-links a:hover {
                    text-decoration: underline;
                }
                
                .social-links {
                    margin-top: 20px;
                }
                
                .social-links a {
                    display: inline-block;
                    width: 36px;
                    height: 36px;
                    background: #667eea;
                    color: white;
                    border-radius: 50%%;
                    text-align: center;
                    line-height: 36px;
                    margin: 0 8px;
                    text-decoration: none;
                    transition: background 0.2s ease;
                }
                
                .social-links a:hover {
                    background: #764ba2;
                }
                
                @media (max-width: 600px) {
                    body {
                        padding: 10px;
                    }
                    
                    .email-wrapper {
                        border-radius: 12px;
                    }
                    
                    .email-header {
                        padding: 30px 20px;
                    }
                    
                    .brand-name {
                        font-size: 24px;
                    }
                    
                    .email-content {
                        padding: 30px 20px;
                    }
                    
                    .content-title {
                        font-size: 20px;
                    }
                    
                    .content-body {
                        font-size: 15px;
                    }
                    
                    .email-footer {
                        padding: 20px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="email-wrapper">
                <div class="email-header">
                    <div class="logo-container">
                        <div class="logo">
                            <img src="https://cdn-icons-png.flaticon.com/512/8913/8913728.png" alt="G-English Logo">
                        </div>
                    </div>
                    <h1 class="brand-name">G-English</h1>
                    <p class="brand-tagline">Tuition Manager System</p>
                </div>
                
                <div class="email-content">
                    <h2 class="content-title">%s</h2>
                    <div class="content-body">
                        <p>%s</p>
                    </div>
                    
                    <div class="cta-section">
                        <a href="#" class="cta-button">Truy cập hệ thống</a>
                    </div>
                    
                    <div class="divider"></div>
                    
                    <p style="color: #718096; font-size: 14px; text-align: center;">
                        Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email hoặc điện thoại.
                    </p>
                </div>
                
                <div class="email-footer">
                    <div class="footer-content">
                        <p><strong>G-English Tuition Manager</strong></p>
                        <p>Hệ thống quản lý học phí tiếng Anh chuyên nghiệp</p>
                        
                        <div class="footer-links">
                            <a href="#">Trang chủ</a>
                            <a href="#">Hỗ trợ</a>
                            <a href="#">Liên hệ</a>
                        </div>
                        
                        <div class="social-links">
                            <a href="#" title="Facebook">f</a>
                            <a href="#" title="Twitter">t</a>
                            <a href="#" title="Instagram">i</a>
                        </div>
                        
                        <p style="margin-top: 20px; font-size: 12px; color: #a0aec0;">
                            Đây là email tự động từ hệ thống G-English. Vui lòng không trả lời.<br>
                            © 2025 G-English. Mọi quyền được bảo lưu.
                        </p>
                    </div>
                </div>
            </div>
        </body>
        </html>
        """;

        // Use String.format() instead of String.formatted() to avoid format flag issues
        return String.format(htmlTemplate, subject, body);
    }
}
