package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.AUTH_API;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.NOTIFICATION_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(NOTIFICATION_API)
public class NotificationController {
    private final NotificationService notificationService;
}
