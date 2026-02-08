package com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response;

public record InvoiceNotifyDTO(
        Long studentId,
        String studentEmail,
        String studentName,
        String className,
        String invoiceId,
        String invoiceContent,
        String amount,
        String paidAt
) {}
