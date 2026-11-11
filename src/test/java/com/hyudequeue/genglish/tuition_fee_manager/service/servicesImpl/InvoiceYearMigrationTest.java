package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceUpdateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.*;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceYearMigrationTest {

    @Test
    @DisplayName("Edge Case 3: Tutor class invoice for month 12 2025 created in Jan 2026 must be assigned year 2025")
    void testTutorClassMonth12CreatedInJan2026ResolvesTo2025() {
        Classes tutorClass = Classes.builder()
                .classId(10L)
                .className("Tutor 1-1 IELTS")
                .amount(BigDecimal.valueOf(2000000))
                .status(ClassStatusEnum.ACTIVE)
                .effectiveFrom(LocalDate.of(2025, 1, 1))
                .build();

        Invoice invoice = Invoice.builder()
                .invoiceId(1L)
                .classes(tutorClass)
                .month(12)
                .dueDate(LocalDate.of(2026, 1, 15))
                .createdAt(LocalDateTime.of(2026, 1, 5, 10, 0))
                .status(InvoiceStatusEnum.PAID)
                .totalAmount(2000000)
                .build();

        Integer year = InvoiceServiceImpl.calculateBillingYearForLegacyInvoice(invoice);
        assertEquals(2025, year, "Tutor invoice for month 12 created in Jan 2026 must have billing year 2025");
    }

    @Test
    @DisplayName("Edge Case 3: Invoice with category 'Tutor' for month 12 created in Jan 2026 must resolve to 2025")
    void testInvoiceWithTutorCategoryMonth12CreatedInJan2026() {
        Classes normalClass = Classes.builder()
                .classId(11L)
                .className("General English")
                .amount(BigDecimal.valueOf(1500000))
                .status(ClassStatusEnum.ACTIVE)
                .effectiveFrom(LocalDate.of(2025, 1, 1))
                .build();

        InvoiceCategory tutorCat = InvoiceCategory.builder()
                .categoryId(1L)
                .name("Tutor Fee")
                .color("#123456")
                .build();

        Invoice invoice = Invoice.builder()
                .invoiceId(2L)
                .classes(normalClass)
                .categories(List.of(tutorCat))
                .month(12)
                .dueDate(LocalDate.of(2026, 1, 10))
                .createdAt(LocalDateTime.of(2026, 1, 2, 8, 30))
                .status(InvoiceStatusEnum.UNPAID)
                .totalAmount(1500000)
                .build();

        Integer year = InvoiceServiceImpl.calculateBillingYearForLegacyInvoice(invoice);
        assertEquals(2025, year, "Invoice with Tutor category for month 12 created in Jan 2026 must have year 2025");
    }

    @Test
    @DisplayName("Rule 2: Normal invoice without Tutor edge case matches dueDate year")
    void testNormalInvoiceMatchesDueDateYear() {
        Classes normalClass = Classes.builder()
                .classId(12L)
                .className("Communication English A1")
                .amount(BigDecimal.valueOf(1200000))
                .status(ClassStatusEnum.ACTIVE)
                .effectiveFrom(LocalDate.of(2025, 1, 1))
                .build();

        // Month 10 with dueDate in Oct 2025
        Invoice inv2025 = Invoice.builder()
                .invoiceId(3L)
                .classes(normalClass)
                .month(10)
                .dueDate(LocalDate.of(2025, 10, 31))
                .createdAt(LocalDateTime.of(2025, 10, 1, 9, 0))
                .build();

        assertEquals(2025, InvoiceServiceImpl.calculateBillingYearForLegacyInvoice(inv2025));

        // Month 3 with dueDate in Mar 2026
        Invoice inv2026 = Invoice.builder()
                .invoiceId(4L)
                .classes(normalClass)
                .month(3)
                .dueDate(LocalDate.of(2026, 3, 31))
                .createdAt(LocalDateTime.of(2026, 3, 1, 9, 0))
                .build();

        assertEquals(2026, InvoiceServiceImpl.calculateBillingYearForLegacyInvoice(inv2026));
    }

    @Test
    @DisplayName("InvoiceUpdateRequestDto applies year update")
    void testUpdateRequestDtoAppliesYear() {
        Invoice invoice = Invoice.builder()
                .invoiceId(5L)
                .month(11)
                .year(2025)
                .dueDate(LocalDate.of(2025, 11, 30))
                .totalAmount(1000000)
                .build();

        InvoiceUpdateRequestDto dto = InvoiceUpdateRequestDto.builder()
                .month(12)
                .year(2026)
                .dueDate(LocalDate.of(2026, 12, 31))
                .build();

        dto.applyTo(invoice, null);

        assertEquals(12, invoice.getMonth());
        assertEquals(2026, invoice.getYear());
        assertEquals(LocalDate.of(2026, 12, 31), invoice.getDueDate());
    }

    @Test
    @DisplayName("InvoiceResponseDto correctly maps month and year")
    void testInvoiceResponseDtoMapping() {
        User user = User.builder().userId(1L).fullName("Nguyễn Văn A").build();
        Classes classes = Classes.builder().classId(2L).className("IELTS Master").build();

        Invoice invoice = Invoice.builder()
                .invoiceId(200L)
                .user(user)
                .classes(classes)
                .month(12)
                .year(2025)
                .dueDate(LocalDate.of(2026, 1, 10))
                .status(InvoiceStatusEnum.PAID)
                .totalAmount(1800000)
                .paymentType(PaymentMethodEnum.BANKING)
                .items(Collections.emptyList())
                .categories(Collections.emptyList())
                .createdAt(LocalDateTime.of(2025, 12, 1, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 10, 10, 0))
                .build();

        InvoiceResponseDto dto = InvoiceResponseDto.toDto(invoice);

        assertNotNull(dto);
        assertEquals(12, dto.getMonth());
        assertEquals(2025, dto.getYear());
        assertEquals(LocalDate.of(2026, 1, 10), dto.getDueDate());
    }

    @Test
    @DisplayName("Invoice getInvoiceContent() includes month and year when both are present")
    void testInvoiceContentIncludesMonthAndYear() {
        Classes classes = Classes.builder().classId(2L).className("IELTS Master").build();

        Invoice invoice = Invoice.builder()
                .invoiceId(300L)
                .classes(classes)
                .month(12)
                .year(2025)
                .items(Collections.emptyList())
                .build();

        assertEquals("Học phí IELTS Master tháng 12/2025", invoice.getInvoiceContent());
    }
}
