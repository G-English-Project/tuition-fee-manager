package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceItemRequestDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.StudentInvoiceRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {

    InvoiceResponseDto createInvoiceForStudent(Long userId,
                                               Long classId,
                                               Integer month,
                                               LocalDate dueDate,
                                               List<InvoiceItemRequestDTO> items);

    InvoiceResponseDto createInvoiceForStudent(Long userId,
                                               Long classId,
                                               Integer month,
                                               LocalDate dueDate,
                                               List<InvoiceItemRequestDTO> items,
                                               List<Long> categoryIds,
                                               PaymentMethodEnum paymentType);

    Page<InvoiceResponseDto> createInvoicesForClass(
            Long classId,
            Integer month,
            LocalDate dueDate,
            List<StudentInvoiceRequest> studentRequests
    );


    Page<InvoiceResponseDto> getInvoicesByClass(Long classId, Pageable pageable);

    Page<InvoiceResponseDto> getInvoicesByStudent(Long userId, Pageable pageable);

    InvoiceResponseDto updateInvoice(Long invoiceId, List<InvoiceItemRequestDTO> updatedItems);

    Page<InvoiceResponseDto> getAllInvoices(Pageable pageable);

    Page<InvoiceResponseDto> getInvoicesByStatus(Pageable pageable, InvoiceStatusEnum invoiceStatus);

    void deleteInvoice(Long invoiceId);

    void processInvoiceStatus(Long invoiceId, InvoiceStatusEnum invoiceStatus);
    InvoiceResponseDto getInvoiceById(Long invoiceId);
}
