package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceNotifyDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.*;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.*;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    public void notifyPaymentSuccess(InvoiceNotifyDTO dto) {

        Map<String, String> values = Map.of(
                "studentName", dto.studentName(),
                "className", dto.className(),
                "invoiceId", dto.invoiceId(),
                "invoiceContent", dto.invoiceContent(),
                "amount", dto.amount(),
                "paidAt", dto.paidAt()
        );

        // ===== STUDENT =====
        notificationService.createNotification(
                dto.studentId(),   // ✅ không cần entity
                NotificationTemplateBuilder.buildSubject(
                        NotificationTemplateEnum.STUDENT_PAID_INVOICE, values
                ),
                NotificationTemplateBuilder.buildBody(
                        NotificationTemplateEnum.STUDENT_PAID_INVOICE, values
                )
        );

        emailService.sendNotificationEmail(
                dto.studentEmail(),  // ✅ lấy từ DTO
                NotificationTemplateEnum.STUDENT_PAID_INVOICE,
                values
        );

        // ===== ADMIN =====
        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        for (User admin : admins) {
            notificationService.createNotification(
                    admin.getUserId(),
                    NotificationTemplateBuilder.buildSubject(
                            NotificationTemplateEnum.STUDENT_PAID_INVOICE, values
                    ),
                    NotificationTemplateBuilder.buildBody(
                            NotificationTemplateEnum.STUDENT_PAID_INVOICE, values
                    )
            );

            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.STUDENT_PAID_INVOICE,
                    values
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
    public void notifyManualConfirm(InvoiceNotifyDTO dto) {

        Map<String, String> values = Map.of(
                "studentName", dto.studentName(),
                "className", dto.className(),
                "invoiceId", dto.invoiceId(),
                "invoiceContent", dto.invoiceContent(),
                "amount", dto.amount(),
                "paidAt", dto.paidAt()
        );

        // ===== STUDENT =====
        notificationService.createNotification(
                dto.studentId(),
                NotificationTemplateBuilder.buildSubject(
                        NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT, values
                ),
                NotificationTemplateBuilder.buildBody(
                        NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT, values
                )
        );

        emailService.sendNotificationEmail(
                dto.studentEmail(),
                NotificationTemplateEnum.STUDENT_SUCCESSFUL_PAYMENT,
                values
        );

        // ===== ADMIN =====
        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
        for (User admin : admins) {

            notificationService.createNotification(
                    admin.getUserId(),
                    NotificationTemplateBuilder.buildSubject(
                            NotificationTemplateEnum.STUDENT_PAID_INVOICE, values
                    ),
                    NotificationTemplateBuilder.buildBody(
                            NotificationTemplateEnum.STUDENT_PAID_INVOICE, values
                    )
            );

            emailService.sendNotificationEmail(
                    admin.getEmail(),
                    NotificationTemplateEnum.STUDENT_PAID_INVOICE,
                    values
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

    private String toStars(String enumName) {
        // Lấy số trong enum, ví dụ: LEVEL_3 → 3
        int score = enumName.replaceAll("\\D", "").isEmpty()
                ? 0 : Integer.parseInt(enumName.replaceAll("\\D", ""));
        return "⭐".repeat(Math.max(0, score));
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

            values.put("attendance", mapAttendance(report.getAttendance()));
            values.put("homework", mapHomework(report.getHomework()));
            values.put("participation", mapParticipation(report.getParticipation()));


            // ✅ chỉ skillProgress hiển thị sao
            values.put("skillProgress", "⭐".repeat(report.getSkillProgress()));

            values.put("areasForImprovement", report.getAreasForImprovement());
            values.put("recommendedAction", report.getRecommendedAction());
            values.put("createdAt", report.getCreatedAt().toString());

            // ===== HÌNH ẢNH =====
            String imageTag = "";
            String base64Image = null;
            String mimeType = null;

            if (Boolean.TRUE.equals(report.getHasImage()) && report.getImage() != null) {
                imageTag = "<img src=\"cid:reportImage\" alt=\"Report Image\" style=\"max-width:100%;border-radius:10px;margin-top:10px;\"/>";
                base64Image = report.getImage().getImageBase64();

                String rawMime = report.getImage().getMimeType();
                if (rawMime == null || rawMime.isBlank()) {
                    mimeType = "image/webp";
                } else if (!rawMime.contains("/")) {
                    mimeType = "image/" + rawMime.toLowerCase();
                } else {
                    mimeType = rawMime.toLowerCase();
                }
            }

            values.put("reportImage", imageTag);

            // ===== SUBJECT =====
            String subject = "📄 Báo cáo học tập - " + student.getFullName() + " (" + clazz.getClassName() + ")";

            // ===== TEMPLATE EMAIL =====
            String body = """
                    <html>
                    <body style='font-family: Arial, sans-serif; background: #f6f6f6; padding: 20px;'>
                        <div style='background: white; border-radius: 10px; padding: 20px;'>
                            <h2 style='color:#2a7ae2;'>BÁO CÁO HỌC TẬP</h2>
                            <p><b>Học sinh:</b> %s</p>
                            <p><b>Lớp:</b> %s</p>
                            <p><b>Giáo viên phụ trách:</b> %s</p>
                            <hr/>
                            <p><b>Chuyên cần:</b> %s</p>
                            <p><b>Bài tập về nhà:</b> %s</p>
                            <p><b>Thái độ trên lớp:</b> %s</p>
                            <p><b>Tiến bộ kỹ năng:</b> %s</p>

                            <h3>Nhận xét</h3>
                            <p>%s</p>

                            <h3>Cần cải thiện</h3>
                            <p>%s</p>

                            %s
                            <p style='font-size: 0.9em; color: gray;'>Ngày tạo báo cáo: %s</p>
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

            // ===== NOTIFY =====
            String notifTitle = "Báo cáo học tập mới";
            String notifContent = "Giáo viên " + teacher.getFullName() + " đã tạo báo cáo học tập cho học sinh "
                    + student.getFullName() + " (Lớp " + clazz.getClassName() + ").";

            // Học sinh
            notificationService.createNotification(student.getUserId(), notifTitle, notifContent);
            emailService.sendHtmlEmailWithInlineImage(student.getEmail(), subject, body, base64Image, mimeType);

            // Giáo viên
            notificationService.createNotification(teacher.getUserId(), notifTitle, notifContent);
            emailService.sendHtmlEmailWithInlineImage(teacher.getEmail(), subject, body, base64Image, mimeType);

            // Admin
            List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);
            for (User admin : admins) {
                notificationService.createNotification(admin.getUserId(), notifTitle, notifContent);
                emailService.sendHtmlEmailWithInlineImage(admin.getEmail(), subject, body, base64Image, mimeType);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toStars(int count) {
        return "⭐".repeat(Math.max(count, 1));
    }

    private String mapAttendance(AttendanceEnum e) {
        return switch (e) {
            case EXCELLENT -> toStars(5);
            case GOOD -> toStars(4);
            case NORMAL -> toStars(3);
            case NEEDS_IMPROVEMENT -> toStars(2);
            case POOR -> toStars(1);
        };
    }

    private String mapHomework(HomeworkEnum e) {
        return switch (e) {
            case ALWAYS_COMPLETES -> toStars(5);
            case USUALLY_COMPLETES -> toStars(4);
            case NORMAL -> toStars(3);
            case OFTEN_INCOMPLETE -> toStars(2);
            case RARELY_COMPLETES -> toStars(1);
        };
    }

    private String mapParticipation(ParticipationEnum e) {
        return switch (e) {
            case VERY_ACTIVE -> toStars(5);
            case ACTIVE -> toStars(4);
            case NORMAL -> toStars(3);
            case PASSIVE -> toStars(2);
            case VERY_PASSIVE -> toStars(1);
        };
    }

    @Async
    @Scheduled(cron = "0 0 9 1 1/2 *")
    public void remindActiveStudentsFeedback() {
        // Lấy danh sách học viên ACTIVE
        List<User> allStudents = userRepository.findByRole(RoleEnum.STUDENT);

        String feedbackLink = "https://portal.gsenglish.org";

        allStudents.stream()
                .filter(u -> u.getStudentStatus() == StudentStatusEnum.ACTIVE)
                .filter(u -> u.getStatus().name().equals("ACTIVE"))
                .forEach(student -> {

                    Map<String, String> values = Map.of(
                            "studentName", student.getFullName(),
                            "feedbackLink", feedbackLink
                    );

                    // Create notification
                    notificationService.createNotification(
                            student.getUserId(),
                            "Nhắc nhở điền feedback định kỳ",
                            "Đã đến lúc điền feedback. Vui lòng xem email để biết chi tiết!"
                    );

                    // Send email
                    emailService.sendNotificationEmail(
                            student.getEmail(),
                            NotificationTemplateEnum.FEEDBACK_REMINDER,
                            values
                    );
                });
    }
}
