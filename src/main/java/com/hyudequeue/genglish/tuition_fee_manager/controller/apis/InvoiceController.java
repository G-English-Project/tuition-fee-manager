package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.InvoiceItemRequestDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.request.StudentInvoiceRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.InvoiceResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.Invoice.response.RevenueSummaryDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.InvoiceStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.PaymentMethodEnum;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
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
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Class ID") @RequestParam Long classId,
            @Parameter(description = "Month of invoice (1-12)") @RequestParam Integer month,
            @Parameter(description = "Due date (e.g., last day of current month)", example = "2025-08-31")
            @RequestParam LocalDate dueDate,
            @Parameter(description = "Optional category IDs for this invoice")
            @RequestParam(required = false) List<Long> categoryIds,
            @Parameter(description = "Optional payment type (default=BANKING)")
            @RequestParam(required = false) PaymentMethodEnum paymentType,
            @RequestBody(required = false) List<InvoiceItemRequestDTO> items
    ) {
        List<Long> safeCategoryIds = (categoryIds == null) ? Collections.emptyList() : categoryIds;
        return ApiResp.success(
                invoiceService.createInvoiceForStudent(
                        userId, classId, month, dueDate, items, safeCategoryIds, paymentType
                )
        );
    }

    @Operation(summary = "Create invoices for a class (per-student fee list)")
    @PostMapping(CREATE_BULK)
    public ResponseEntity<ApiResp<Page<InvoiceResponseDto>>> createInvoicesForClass(
            @Parameter(description = "Class ID") @RequestParam Long classId,
            @Parameter(description = "Month of invoice (1-12)") @RequestParam Integer month,
            @Parameter(description = "Due date (e.g., last day of current month)", example = "2025-08-31")
            @RequestParam LocalDate dueDate,
            @RequestBody List<StudentInvoiceRequest> studentRequests) {

        return ApiResp.success(
                invoiceService.createInvoicesForClass(classId, month, dueDate, studentRequests)
        );
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

    @Operation(summary = "Get all invoices with optional filters")
    @GetMapping(GET_ALL)
    public ResponseEntity<ApiResp<Page<InvoiceResponseDto>>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) InvoiceStatusEnum status,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Long categoryId
    ) {
        return ApiResp.success(
                invoiceService.getAllInvoices(PageRequest.of(page, size), status, month, categoryId)
        );
    }


    @Operation(summary = "Get invoices by status")
    @GetMapping(GET_BY_STATUS)
    public ResponseEntity<ApiResp<Page<InvoiceResponseDto>>> getInvoicesByStatus(
            @RequestParam InvoiceStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResp.success(invoiceService.getInvoicesByStatus(PageRequest.of(page, size), status));
    }

    @Operation(summary = "Get invoice by ID")
    @GetMapping(GET_BY_ID)
    public ResponseEntity<ApiResp<InvoiceResponseDto>> getInvoiceById(@RequestParam Long invoiceId) {
        return ApiResp.success(invoiceService.getInvoiceById(invoiceId));
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

    @Validated
    @Operation(
            summary = "Revenue summary (groupBy = month | class | week)",
            description = """
        Trả về thống kê doanh thu đã thanh toán (status = PAID), nhóm theo:
        - month: nhóm theo tháng dạng yyyy-MM (mốc thời gian ưu tiên paidAt, fallback dueDate)
        - class: nhóm theo tên lớp (className)
        - week : nhóm theo tuần ISO dạng yyyy-Www

        Ghi chú:
        - Chỉ tính các hóa đơn PAID để phản ánh doanh thu đã ghi nhận.
        - Phân trang trên tập kết quả đã nhóm (không phải trên bản ghi invoice thô).
        - Tham số page/size dùng phân trang chuẩn Spring (page bắt đầu từ 0).
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Trả về Page<RevenueSummaryDto>"),
            @ApiResponse(responseCode = "400", description = "Bad Request - groupBy không hợp lệ hoặc tham số không hợp lệ")
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResp<Page<RevenueSummaryDto>>> getRevenueSummary(
            @Parameter(
                    name = "groupBy",
                    description = "Kiểu nhóm dữ liệu",
                    example = "month",
                    required = false,
                    schema = @Schema(allowableValues = {"month", "class", "week"}, defaultValue = "month")
            )
            @RequestParam(defaultValue = "month") String groupBy,

            @Parameter(
                    name = "page",
                    description = "Trang (bắt đầu từ 0)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page phải >= 0")
            int page,

            @Parameter(
                    name = "size",
                    description = "Số phần tử mỗi trang (1–200)",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            @Min(value = 1,  message = "size phải >= 1")
            @Max(value = 200, message = "size tối đa 200")
            int size
    ) {
        // Chuẩn hóa đầu vào: cắt trim và lower-case để so khớp dễ dàng
        final String key = groupBy == null ? "month" : groupBy.trim().toLowerCase();

        // Tạo PageRequest dùng chung
        final PageRequest pageable = PageRequest.of(page, size);

        // Điều hướng tới service phù hợp; ném 400 nếu không hợp lệ
        switch (key) {
            case "month":
                return ApiResp.success(invoiceService.getRevenueSummaryByMonth(pageable));
            case "class":
                return ApiResp.success(invoiceService.getRevenueSummaryByClass(pageable));
            case "week":
                return ApiResp.success(invoiceService.getRevenueSummaryByWeek(pageable));
            default:
                // Nên dùng ResponseStatusException để Swagger hiển thị response 400 đẹp
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid groupBy. Use one of: month | class | week"
                );
        }
    }

}
