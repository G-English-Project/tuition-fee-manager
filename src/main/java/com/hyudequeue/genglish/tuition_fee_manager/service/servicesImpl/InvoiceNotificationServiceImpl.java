package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.entities.*;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceNotificationServiceImpl {
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService;
    private final UserRepository userRepository;

    @Async
    public void notifyInvoiceCreated(User user, Integer month) {
        Map<String, String> values = Map.of(
                "studentName", user.getFullName(),
                "invoiceContent", "Học phí tháng " + month
        );

        // Notify & Email student
        String studentSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.NEW_INVOICE_NOTIFICATION, values
        );
        String studentBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.NEW_INVOICE_NOTIFICATION, values
        );
        notificationService.createNotification(user.getUserId(), studentSubject, studentBody);
        emailService.sendNotificationEmail(
                user.getEmail(),
                NotificationTemplateEnum.NEW_INVOICE_NOTIFICATION,
                values
        );

        // Notify & Email teacher/admin
        String adminSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.STUDENT_INVOICE_CREATED, values
        );
        String adminBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.STUDENT_INVOICE_CREATED, values
        );

        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        for (User admin : admins) {
            notificationService.createNotification(admin.getUserId(), adminSubject, adminBody);
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.STUDENT_INVOICE_CREATED,
                    values
            );
        }
    }

    @Async
    public void sendClassInvoiceNotificationsAsync(List<Invoice> invoices, Classes classes) {
        // Gửi cho từng học sinh
        invoices.forEach(invoice -> {
            User student = invoice.getUser();
            Map<String, String> values = Map.of(
                    "studentName", student.getFullName(),
                    "invoiceContent", "Học phí tháng " + invoice.getMonth()
            );

            String studentSubject = NotificationTemplateBuilder.buildSubject(
                    NotificationTemplateEnum.NEW_INVOICE_NOTIFICATION, values
            );
            String studentBody = NotificationTemplateBuilder.buildBody(
                    NotificationTemplateEnum.NEW_INVOICE_NOTIFICATION, values
            );

            notificationService.createNotification(student.getUserId(), studentSubject, studentBody);
            emailService.sendNotificationEmail(
                    student.getEmail(),
                    NotificationTemplateEnum.NEW_INVOICE_NOTIFICATION,
                    values
            );
        });

        // Gửi cho admin (1 lần)
        Map<String, String> adminValues = Map.of(
                "className", classes.getClassName()
        );

        String adminSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.CLASS_INVOICE_CREATED, adminValues
        );
        String adminBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.CLASS_INVOICE_CREATED, adminValues
        );

        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        for (User admin : admins) {
            notificationService.createNotification(admin.getUserId(), adminSubject, adminBody);
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.CLASS_INVOICE_CREATED,
                    adminValues
            );
        }
    }


    // Notify khi invoice bị hủy
    @Async
    public void notifyInvoiceCancelled(Invoice invoice) {
        Map<String, String> values = Map.of(
                "invoiceId", invoice.getInvoiceId().toString(),
                "className", invoice.getClasses().getClassName(),
                "invoiceContent", "Hóa đơn #" + invoice.getInvoiceId() + " (" + invoice.getClasses().getClassName() + ")"
        );

        String adminSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.INVOICE_CANCELLED_ALERT, values
        );
        String adminBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.INVOICE_CANCELLED_ALERT, values
        );

        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);

        admins.forEach(admin -> {
            notificationService.createNotification(admin.getUserId(), adminSubject, adminBody);
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.INVOICE_CANCELLED_ALERT,
                    values
            );
        });
    }

    @Async
    public void notifyPaymentSuccess(Invoice invoice, Payment payment) {
        // --- Notify Student ---
        Long studentId = invoice.getUser().getUserId();
        Map<String, String> studentValues = Map.of(
                "studentName", invoice.getUser().getFullName(),
                "invoiceId", String.valueOf(invoice.getInvoiceId()),
                "amount", String.valueOf(payment.getAmount()),
                "invoiceContent", invoice.getInvoiceContent()
        );

        String studentSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.STUDENT_PAID_INVOICE, studentValues
        );
        String studentBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.STUDENT_PAID_INVOICE, studentValues
        );

        notificationService.createNotification(studentId, studentSubject, studentBody);
        emailService.sendNotificationEmail(
                invoice.getUser().getEmail(),
                NotificationTemplateEnum.STUDENT_PAID_INVOICE,
                studentValues
        );

        // --- Notify Admin ---
        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        for (User admin : admins) {
            Map<String, String> adminValues = Map.of(
                    "teacherName", admin.getFullName(),
                    "studentName", invoice.getUser().getFullName(),
                    "invoiceId", String.valueOf(invoice.getInvoiceId()),
                    "amount", String.valueOf(payment.getAmount()),
                    "invoiceContent", invoice.getInvoiceContent()
            );

            String adminSubject = NotificationTemplateBuilder.buildSubject(
                    NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT, adminValues
            );
            String adminBody = NotificationTemplateBuilder.buildBody(
                    NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT, adminValues
            );

            notificationService.createNotification(admin.getUserId(), adminSubject, adminBody);
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT,
                    adminValues
            );
        }
    }

    @Async
    public void notifyClassFeeUpdated(Classes classes, Integer newAmount) {
        // Notify & Email all students in this class
        List<User> students = userRepository.findAllByEnrolledClass(classes);
        for (User student : students) {
            Map<String, String> studentValues = Map.of(
                    "studentName", student.getFullName(),
                    "className", classes.getClassName(),
                    "newAmount", String.valueOf(newAmount)
            );

            String studentSubject = NotificationTemplateBuilder.buildSubject(
                    NotificationTemplateEnum.STUDENT_TUITION_EDITED, studentValues
            );
            String studentBody = NotificationTemplateBuilder.buildBody(
                    NotificationTemplateEnum.STUDENT_TUITION_EDITED, studentValues
            );

            notificationService.createNotification(student.getUserId(), studentSubject, studentBody);
            emailService.sendNotificationEmail(
                    student.getEmail(),
                    NotificationTemplateEnum.STUDENT_TUITION_EDITED,
                    studentValues
            );
        }

        // Notify & Email all admins once
        Map<String, String> adminValues = Map.of(
                "className", classes.getClassName(),
                "newAmount", String.valueOf(newAmount)
        );

        String adminSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.CLASS_TUITION_UPDATED, adminValues
        );
        String adminBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.CLASS_TUITION_UPDATED, adminValues
        );

        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        for (User admin : admins) {
            notificationService.createNotification(admin.getUserId(), adminSubject, adminBody);
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.CLASS_TUITION_UPDATED,
                    adminValues
            );
        }
    }

    @Async
    public void notifyStudentAssignedToClass(User student, Classes classes) {
        Map<String, String> values = Map.of(
                "studentName", student.getFullName(),
                "className", classes.getClassName()
        );

        String studentSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.STUDENT_ADDED_TO_CLASS, values
        );
        String studentBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.STUDENT_ADDED_TO_CLASS, values
        );

        notificationService.createNotification(student.getUserId(), studentSubject, studentBody);
        emailService.sendNotificationEmail(
                student.getEmail(),
                NotificationTemplateEnum.STUDENT_ADDED_TO_CLASS,
                values
        );
    }

    @Async
    public void notifyStudentRemovedFromClass(User student, Classes classes) {
        Map<String, String> values = Map.of(
                "studentName", student.getFullName(),
                "className", classes.getClassName()
        );

        // Notify student
        String studentSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.STUDENT_REMOVED_FROM_CLASS_STUDENT, values
        );
        String studentBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.STUDENT_REMOVED_FROM_CLASS_STUDENT, values
        );
        notificationService.createNotification(student.getUserId(), studentSubject, studentBody);
        emailService.sendNotificationEmail(
                student.getEmail(),
                NotificationTemplateEnum.STUDENT_REMOVED_FROM_CLASS_STUDENT,
                values
        );

        // Notify admins
        String adminSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.STUDENT_REMOVED_FROM_CLASS, values
        );
        String adminBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.STUDENT_REMOVED_FROM_CLASS, values
        );

        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        admins.forEach(admin -> {
            notificationService.createNotification(admin.getUserId(), adminSubject, adminBody);
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.STUDENT_REMOVED_FROM_CLASS,
                    values
            );
        });
    }

    @Async
    public void notifyManualConfirm(Invoice invoice) {
        // --- Notify Student ---
        Long studentId = invoice.getUser().getUserId();
        Map<String, String> studentValues = Map.of(
                "studentName", invoice.getUser().getFullName(),
                "invoiceId", String.valueOf(invoice.getInvoiceId()),
                "amount", String.valueOf(invoice.getTotalAmount())
        );

        String studentSubject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT, studentValues
        );
        String studentBody = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT, studentValues
        );

        notificationService.createNotification(studentId, studentSubject, studentBody);
        emailService.sendNotificationEmail(
                invoice.getUser().getEmail(),
                NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT,
                studentValues
        );

        // --- Notify Admin ---
        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        for (User admin : admins) {
            Map<String, String> adminValues = Map.of(
                    "teacherName", admin.getFullName(),
                    "studentName", invoice.getUser().getFullName(),
                    "invoiceId", String.valueOf(invoice.getInvoiceId()),
                    "amount", String.valueOf(invoice.getTotalAmount())
            );

            String adminSubject = NotificationTemplateBuilder.buildSubject(
                    NotificationTemplateEnum.STUDENT_PAID_INVOICE, adminValues
            );
            String adminBody = NotificationTemplateBuilder.buildBody(
                    NotificationTemplateEnum.STUDENT_PAID_INVOICE, adminValues
            );

            notificationService.createNotification(admin.getUserId(), adminSubject, adminBody);
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.STUDENT_PAID_INVOICE,
                    adminValues
            );
        }
    }


    @Async
    public void notifyAdminNewFeedback(User student, Classes classes, String content) {
        Map<String, String> values = Map.of(
                "studentName", student.getFullName(),
                "className", classes.getClassName(),
                "feedbackContent", content
        );

        String subject = NotificationTemplateBuilder.buildSubject(
                NotificationTemplateEnum.NEW_FEEDBACK_RECEIVED, values
        );
        String body = NotificationTemplateBuilder.buildBody(
                NotificationTemplateEnum.NEW_FEEDBACK_RECEIVED, values
        );

        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        for (User admin : admins) {
            notificationService.createNotification(admin.getUserId(), subject, body);
            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.NEW_FEEDBACK_RECEIVED,
                    values
            );
        }
    }

    @Async
    public void notifyReport(Report report) {
        try {
            User student = report.getStudent();
            User teacher = report.getTeacher();
            Classes clazz = report.getClassRoom();

            Map<String, String> values = new HashMap<>();
            values.put("studentName", student.getFullName());
            values.put("className", clazz.getClassName());
            values.put("teacherName", teacher.getFullName());
            values.put("attendance", report.getAttendance().name());
            values.put("homework", report.getHomework().name());
            values.put("participation", report.getParticipation().name());
            values.put("skillProgress", String.valueOf(report.getSkillProgress()));
            values.put("areasForImprovement", report.getAreasForImprovement());
            values.put("recommendedAction", report.getRecommendedAction());
            values.put("createdAt", report.getCreatedAt().toString());

            // Nếu report có ảnh thì gắn vào email
            String imageTag = "";
            if (Boolean.TRUE.equals(report.getHasImage()) && report.getImage() != null) {
                ReportImage img = report.getImage();
                String mime = (img.getMimeType() == null || img.getMimeType().isEmpty()) ? "image/webp" : img.getMimeType();
                String imgB64 = img.getImageBase64();
                if (imgB64 != null && !imgB64.isEmpty()) {
                    imageTag = "<img src=\"data:" + mime + ";base64," + imgB64 + "\" " +
                            "alt=\"Report Image\" style=\"max-width:100%;border-radius:10px;margin-top:10px;\"/>";
                }
            }
            values.put("reportImage", imageTag);

            // Build subject & body
            String subject = "📄 Student Report - " + student.getFullName() + " (" + clazz.getClassName() + ")";
            String body = """
            <html>
            <body style='font-family: Arial, sans-serif; background: #f6f6f6; padding: 20px;'>
                <div style='background: white; border-radius: 10px; padding: 20px;'>
                    <h2>Student Report</h2>
                    <p><b>Student:</b> %s</p>
                    <p><b>Class:</b> %s</p>
                    <p><b>Teacher:</b> %s</p>
                    <hr/>
                    <p><b>Attendance:</b> %s</p>
                    <p><b>Homework:</b> %s</p>
                    <p><b>Participation:</b> %s</p>
                    <p><b>Skill Progress:</b> %s%%</p>
                    <h3>Areas for Improvement</h3>
                    <p>%s</p>
                    <h3>Recommended Actions</h3>
                    <p>%s</p>
                    %s
                    <p style='font-size: 0.9em; color: gray;'>Created at: %s</p>
                </div>
            </body>
            </html>
            """.formatted(
                    values.get("studentName"),
                    values.get("className"),
                    values.get("teacherName"),
                    values.get("attendance"),
                    values.get("homework"),
                    values.get("participation"),
                    values.get("skillProgress"),
                    values.get("areasForImprovement"),
                    values.get("recommendedAction"),
                    values.get("reportImage"),
                    values.get("createdAt")
            );

            // ===== Notify & Send Email =====
            String notifTitle = "Báo cáo học tập mới từ " + teacher.getFullName();
            String notifContent = "Giáo viên " + teacher.getFullName() + " vừa tạo báo cáo học tập cho học sinh "
                    + student.getFullName() + " trong lớp " + clazz.getClassName() + ".";

            // Notify học sinh
            notificationService.createNotification(student.getUserId(), notifTitle, notifContent);
            emailService.sendHtmlEmail(student.getEmail(), subject, body);

            // Notify giáo viên
            notificationService.createNotification(teacher.getUserId(), notifTitle, notifContent);
            emailService.sendHtmlEmail(teacher.getEmail(), subject, body);

            // Notify admin
            List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
            for (User admin : admins) {
                notificationService.createNotification(admin.getUserId(), notifTitle, notifContent);
                emailService.sendHtmlEmail(admin.getEmail(), subject, body);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Có thể thay bằng logger.error("Failed to send report notification", e);
        }
    }
}
