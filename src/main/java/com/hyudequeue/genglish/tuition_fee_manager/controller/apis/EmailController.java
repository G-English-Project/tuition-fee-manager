package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;
import com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl.EmailServiceImpl;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.NotificationTemplateEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailServiceImpl emailService;

    public EmailController(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(
            @RequestParam String receiverEmail,
            @RequestParam NotificationTemplateEnum template,
            @RequestBody Map<String, String> values
    ) {
        emailService.sendNotificationEmail(receiverEmail, template, values);
        return ResponseEntity.ok("Email sent successfully to " + receiverEmail);
    }
}
