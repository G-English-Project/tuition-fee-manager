package com.hyudequeue.genglish.tuition_fee_manager.utility.commandLineRunners;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl.EmailServiceImpl;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InvoiceOverdueMarker {
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailServiceImpl emailService; // thêm email service

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Bangkok")
    @Transactional
    public void markAndNotify() {
        LocalDate today = LocalDate.now();
        List<Long> targetInvoiceIds = invoiceRepository.findIdsDueBeforeAndStatusUnpaid(today);
        if (targetInvoiceIds.isEmpty()) return;

        int affected = invoiceRepository.markOverdueByIds(targetInvoiceIds);
        if (affected <= 0) return;

        // 1. Notify & Email cho ADMIN
        List<User> admins = userRepository.findByRole(RoleEnum.ADMIN);

        if (!admins.isEmpty()) {
            Map<String, String> values = Map.of();
            String subject = NotificationTemplateBuilder.buildSubject(
                    NotificationTemplateEnum.OVERDUE_INVOICE_ALERT, values);
            String body = NotificationTemplateBuilder.buildBody(
                    NotificationTemplateEnum.OVERDUE_INVOICE_ALERT, values);

            for (User admin : admins) {
                notificationService.createNotification(admin.getUserId(), subject, body);
                emailService.sendNotificationEmail(
                        admin.getEmail(),
                        NotificationTemplateEnum.OVERDUE_INVOICE_ALERT,
                        values
                );
            }
        }


        // 2. Notify & Email từng học sinh bị ảnh hưởng
        Map<Long, List<String>> userToInvoiceContents =
                invoiceRepository.mapUserToOverdueInvoiceContents(targetInvoiceIds);

        userToInvoiceContents.forEach((userId, contents) -> {
            String joined = String.join(", ", contents);
            Map<String, String> values = Map.of("invoiceContent", joined);

            String subject = NotificationTemplateBuilder.buildSubject(
                    NotificationTemplateEnum.STUDENT_INVOICE_OVERDUE, values);
            String body = NotificationTemplateBuilder.buildBody(
                    NotificationTemplateEnum.STUDENT_INVOICE_OVERDUE, values);

            notificationService.createNotification(userId, subject, body);

            userRepository.findById(userId).ifPresent(student -> {
                emailService.sendNotificationEmail(
                        student.getEmail(),
                        NotificationTemplateEnum.STUDENT_INVOICE_OVERDUE,
                        values
                );
            });
        });
    }
}



