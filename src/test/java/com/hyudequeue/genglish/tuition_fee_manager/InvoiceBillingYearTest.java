package com.hyudequeue.genglish.tuition_fee_manager;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceUpdateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Invoice;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InvoiceBillingYearTest {

    @Test
    @DisplayName("Case 1: Existing invoice month 12 with dueDate 10/01/2026 and year=null must infer year 2025")
    void testExistingInvoiceDec2025DueJan2026InfersYear2025() {
        Invoice invoice = Invoice.builder()
                .invoiceId(101L)
                .month(12)
                .year(null)
                .dueDate(LocalDate.of(2026, 1, 10))
                .status(InvoiceStatusEnum.PAID)
                .totalAmount(1800000)
                .createdAt(LocalDateTime.of(2025, 12, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 10, 10, 0))
                .build();

        assertEquals(2025, invoice.getYear(), "Billing year for month 12 with dueDate in Jan 2026 must be 2025");
    }

    @Test
    @DisplayName("Case 2: New invoice month 12 with dueDate 02/01/2027 must infer year 2026")
    void testNewInvoiceDec2026DueJan2027InfersYear2026() {
        Invoice invoice = Invoice.builder()
                .invoiceId(102L)
                .month(12)
                .dueDate(LocalDate.of(2027, 1, 2))
                .status(InvoiceStatusEnum.UNPAID)
                .totalAmount(2000000)
                .build();

        assertEquals(2026, invoice.getYear(), "Billing year for month 12 with dueDate in Jan 2027 must be 2026");
    }

    @Test
    @DisplayName("Month 11 with dueDate 10/12/2026 must infer year 2026")
    void testNov2026DueDec2026InfersYear2026() {
        Invoice invoice = Invoice.builder()
                .invoiceId(103L)
                .month(11)
                .dueDate(LocalDate.of(2026, 12, 10))
                .status(InvoiceStatusEnum.UNPAID)
                .totalAmount(1500000)
                .build();

        assertEquals(2026, invoice.getYear(), "Billing year for month 11 with dueDate in Dec 2026 must be 2026");
    }

    @Test
    @DisplayName("Month 1 with dueDate 10/01/2027 must infer year 2027")
    void testJan2027DueJan2027InfersYear2027() {
        Invoice invoice = Invoice.builder()
                .invoiceId(104L)
                .month(1)
                .dueDate(LocalDate.of(2027, 1, 10))
                .status(InvoiceStatusEnum.UNPAID)
                .totalAmount(1500000)
                .build();

        assertEquals(2027, invoice.getYear(), "Billing year for month 1 with dueDate in Jan 2027 must be 2027");
    }

    @Test
    @DisplayName("Explicit year takes precedence over dueDate inference")
    void testExplicitYearTakesPrecedence() {
        Invoice invoice = Invoice.builder()
                .invoiceId(105L)
                .month(12)
                .year(2025)
                .dueDate(LocalDate.of(2026, 1, 10))
                .status(InvoiceStatusEnum.PAID)
                .build();

        assertEquals(2025, invoice.getYear());
    }

    @Test
    @DisplayName("InvoiceResponseDto mapping maps the inferred billing year correctly")
    void testInvoiceResponseDtoMapping() {
        User user = User.builder().userId(1L).fullName("Nguyễn Hoàng Minh Trí").build();
        Classes classes = Classes.builder().classId(2L).className("IELTS Master").build();

        Invoice invoice = Invoice.builder()
                .invoiceId(200L)
                .user(user)
                .classes(classes)
                .month(12)
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
        assertEquals(2025, dto.getYear(), "Dto must expose year 2025 for month 12 with dueDate 10/01/2026");
        assertEquals(LocalDate.of(2026, 1, 10), dto.getDueDate());
    }

    @Test
    @DisplayName("InvoiceUpdateRequestDto applies year and infers when year is null")
    void testInvoiceUpdateRequestDtoApply() {
        Invoice invoice = Invoice.builder()
                .invoiceId(300L)
                .month(12)
                .dueDate(LocalDate.of(2025, 12, 31))
                .build();

        InvoiceUpdateRequestDto updateDto = InvoiceUpdateRequestDto.builder()
                .month(12)
                .dueDate(LocalDate.of(2027, 1, 2))
                .build();

        updateDto.applyTo(invoice, null);

        assertEquals(12, invoice.getMonth());
        assertEquals(2026, invoice.getYear(), "After update with month 12 and dueDate 02/01/2027, year must be inferred as 2026");
    }
}
