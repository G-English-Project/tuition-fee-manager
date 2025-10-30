package com.hyudequeue.genglish.tuition_fee_manager.utility.commandLineRunners;

import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClassExpiredCheckCron {

    private final ClassRepository classRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void run() {
        int updated = classRepository.deactivateExpired(LocalDate.now(), ClassStatusEnum.INACTIVE);
        if (updated > 0) {
            log.info("ClassExpiredCheckCron: chuyển INACTIVE cho {} lớp đã hết hạn.", updated);
        } else {
            log.info("ClassExpiredCheckCron: không có lớp nào hết hạn.");
        }
    }
}
