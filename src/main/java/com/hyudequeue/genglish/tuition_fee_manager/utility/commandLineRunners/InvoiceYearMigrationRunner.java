package com.hyudequeue.genglish.tuition_fee_manager.utility.commandLineRunners;

import com.hyudequeue.genglish.tuition_fee_manager.repository.InvoiceRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
@RequiredArgsConstructor
public class InvoiceYearMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InvoiceYearMigrationRunner.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    @Override
    public void run(String... args) {
        long missingCount = invoiceRepository.countByYearIsNull();
        if (missingCount > 0) {
            log.info("Found {} invoices with missing billing year. Starting one-time migration...", missingCount);
            int migrated = invoiceService.migrateInvoiceYears(false);
            log.info("One-time invoice year migration completed: updated {} invoices.", migrated);
        } else {
            log.info("Invoice year migration check: All invoices have year populated (0 missing). Skipping migration.");
        }
    }
}
