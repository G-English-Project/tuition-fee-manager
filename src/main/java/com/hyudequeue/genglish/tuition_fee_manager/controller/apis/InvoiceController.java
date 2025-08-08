package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceItemRequestDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.InvoiceEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.INVOICE_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(INVOICE_API)
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(summary = "Create invoice for one student")
    @PostMapping(CREATE_ONE)
    public ResponseEntity<ApiResp<InvoiceResponseDto>> createInvoiceForStudent(
            @RequestParam Long userId,
            @RequestParam Long classId,
            @RequestParam Integer month,
            @RequestParam LocalDate dueDate,
            @RequestBody List<InvoiceItemRequestDTO> items) {
        return ApiResp.success(invoiceService.createInvoiceForStudent(userId, classId, month, dueDate, items));
    }

    @Operation(summary = "Create invoices for a class")
    @PostMapping(CREATE_BULK)
    public ResponseEntity<ApiResp<Page<InvoiceResponseDto>>> createInvoicesForClass(
            @RequestParam Long classId,
            @RequestParam Integer month,
            @RequestParam LocalDate dueDate,
            @RequestBody List<InvoiceItemRequestDTO> items) {
        return ApiResp.success(invoiceService.createInvoicesForClass(classId, month, dueDate, items));
    }

    @Operation(summary = "Get invoices by class")
    @GetMapping(GET_BY_CLASS)
    public ResponseEntity<ApiResp<Page<InvoiceResponseDto>>> getInvoicesByClass(
            @RequestParam Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResp.success(invoiceService.getInvoicesByClass(classId, PageRequest.of(page, size)));
    }

    @Operation(summary = "Get invoices by student")
    @GetMapping(GET_BY_STUDENT)
    public ResponseEntity<ApiResp<Page<InvoiceResponseDto>>> getInvoicesByStudent(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResp.success(invoiceService.getInvoicesByStudent(userId, PageRequest.of(page, size)));
    }

    @Operation(summary = "Update invoice items")
    @PutMapping(UPDATE)
    public ResponseEntity<ApiResp<InvoiceResponseDto>> updateInvoice(
            @RequestParam Long invoiceId,
            @RequestBody List<InvoiceItemRequestDTO> updatedItems) {
        return ApiResp.success(invoiceService.updateInvoice(invoiceId, updatedItems));
    }

    @Operation(summary = "Get all invoices")
    @GetMapping(GET_ALL)
    public ResponseEntity<ApiResp<Page<InvoiceResponseDto>>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResp.success(invoiceService.getAllInvoices(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get invoices by status")
    @GetMapping(GET_BY_STATUS)
    public ResponseEntity<ApiResp<Page<InvoiceResponseDto>>> getInvoicesByStatus(
            @RequestParam InvoiceStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResp.success(invoiceService.getInvoicesByStatus(PageRequest.of(page, size), status));
    }

    @Operation(summary = "Delete invoice")
    @DeleteMapping(DELETE)
    public ResponseEntity<ApiResp<String>> deleteInvoice(@RequestParam Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ApiResp.success("Invoice deleted");
    }

    @Operation(summary = "Process invoice status change")
    @PutMapping(PROCESS_STATUS)
    public ResponseEntity<ApiResp<String>> processStatus(
            @RequestParam Long invoiceId,
            @RequestParam InvoiceStatusEnum status) {
        invoiceService.processInvoiceStatus(invoiceId, status);
        return ApiResp.success("Invoice status updated");
    }
}