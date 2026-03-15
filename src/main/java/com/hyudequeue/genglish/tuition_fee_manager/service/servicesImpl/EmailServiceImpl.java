package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.MailTemplateBuilder;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.NotificationTemplateBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class EmailServiceImpl {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotificationEmail(
            String receiverEmail,
            NotificationTemplateEnum template,
            Map<String, String> values
    ) {
        try {
            // DEBUG
            System.out.println("Mail values: " + values);
            // Build subject & HTML body
            String subject = "Mail from G's English";
            String body = MailTemplateBuilder.buildHtml(template, values);

            // Create HTML email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(receiverEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true means HTML

            // Send email
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    public void sendHtmlEmail(String to, String subject, String htmlBody, String base64Image, String mimeType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                helper.addInline("reportImage", new ByteArrayResource(imageBytes), mimeType);
            }

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendHtmlEmailWithInlineImage(String to, String subject, String htmlBody, String base64Image, String mimeType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (base64Image != null && !base64Image.isEmpty()) {

                // ✅ FIX QUAN TRỌNG — loại bỏ xuống dòng & khoảng trắng
                base64Image = base64Image.replaceAll("\\s+", "");

                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                helper.addInline("reportImage", new ByteArrayResource(imageBytes), mimeType);
            }

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
