package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Payment;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
                "amount", String.valueOf(payment.getAmount())
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
                    "amount", String.valueOf(payment.getAmount())
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



}