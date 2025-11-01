package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request.CreateReportRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request.UpdateReportRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.response.ReportImageDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.response.ReportResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ReportEndpoints.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(REPORT_API)
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Create a new report", description = "Creates a new report for a student and class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report created successfully")
    })
    @PostMapping(CREATE_REPORT)
    public ResponseEntity<ApiResp<ReportResponseDTO>> createReport(
            @Parameter(description = "Report creation request data") @RequestBody CreateReportRequest createReportRequest) {

        ReportResponseDTO createdReport = reportService.create(createReportRequest);
        return ApiResp.success(createdReport);
    }

    @Operation(summary = "Update an existing report", description = "Updates a report's details, including title, content, and optional images.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report updated successfully")
    })
    @PutMapping(UPDATE_REPORT)
    public ResponseEntity<ApiResp<ReportResponseDTO>> updateReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId,
            @Parameter(description = "Report update request data") @RequestBody UpdateReportRequest updateReportRequest) {

        ReportResponseDTO updatedReport = reportService.update(reportId, updateReportRequest);
        return ApiResp.success(updatedReport);
    }

    @Operation(summary = "Delete a report", description = "Deletes a report by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report deleted successfully")
    })
    @DeleteMapping(DELETE_REPORT)
    public ResponseEntity<ApiResp<String>> deleteReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId) {

        reportService.delete(reportId);
        return ApiResp.success("Report deleted successfully");
    }

    @Operation(summary = "Get report by ID", description = "Retrieves a report's details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report retrieved successfully")
    })
    @GetMapping(GET_REPORT_BY_ID)
    public ResponseEntity<ApiResp<ReportResponseDTO>> getReportById(
            @Parameter(description = "Report ID") @PathVariable Long reportId) {

        ReportResponseDTO report = reportService.getById(reportId);
        return ApiResp.success(report);
    }

    @Operation(summary = "List reports by student", description = "Lists reports for a specific student, with pagination support.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping(LIST_REPORTS_BY_STUDENT)
    public ResponseEntity<ApiResp<Page<ReportResponseDTO>>> listReportsByStudent(
            @Parameter(description = "Student ID") @PathVariable Long studentId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {

        Page<ReportResponseDTO> reports = reportService.listByStudent(studentId, PageRequest.of(pageNumber, pageSize));
        return ApiResp.success(reports);
    }

    @Operation(summary = "List reports by class", description = "Lists reports for a specific class, with pagination support.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping(LIST_REPORTS_BY_CLASS)
    public ResponseEntity<ApiResp<Page<ReportResponseDTO>>> listReportsByClass(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {

        Page<ReportResponseDTO> reports = reportService.listByClass(classId, PageRequest.of(pageNumber, pageSize));
        return ApiResp.success(reports);
    }

    @Operation(summary = "List reports by student within a date range", description = "Lists reports for a specific student within a date range, with pagination support.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping(LIST_REPORTS_BY_STUDENT_IN_RANGE)
    public ResponseEntity<ApiResp<Page<ReportResponseDTO>>> listReportsByStudentInRange(
            @Parameter(description = "Student ID") @PathVariable Long studentId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);
        Page<ReportResponseDTO> reports = reportService.listByStudentInRange(studentId, fromDateTime, toDateTime, PageRequest.of(pageNumber, pageSize));
        return ApiResp.success(reports);
    }

    @Operation(summary = "List reports by class within a date range", description = "Lists reports for a specific class within a date range, with pagination support.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping(LIST_REPORTS_BY_CLASS_IN_RANGE)
    public ResponseEntity<ApiResp<Page<ReportResponseDTO>>> listReportsByClassInRange(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);
        Page<ReportResponseDTO> reports = reportService.listByClassInRange(classId, fromDateTime, toDateTime, PageRequest.of(pageNumber, pageSize));
        return ApiResp.success(reports);
    }

    @Operation(summary = "Get all reports", description = "Get all reports with filters for class, student, teacher.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping("/all")
    public ResponseEntity<ApiResp<Page<ReportResponseDTO>>> getAllReports(
            @Parameter(description = "Class ID filter") @RequestParam(required = false) Long classId,
            @Parameter(description = "Student ID filter") @RequestParam(required = false) Long studentId,
            @Parameter(description = "Teacher ID filter") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {

        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : null;
        
        Page<ReportResponseDTO> reports = reportService.getAllReports(
                classId, studentId, teacherId, fromDateTime, toDateTime, PageRequest.of(pageNumber, pageSize)
        );
        return ApiResp.success(reports);
    }

    @Operation(summary = "Get report image thumbnail", description = "Retrieves the thumbnail of a report's image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thumbnail retrieved successfully")
    })
    @GetMapping(GET_REPORT_IMAGE_THUMB)
    public ResponseEntity<ApiResp<ReportImageDTO>> getReportImageThumb(
            @Parameter(description = "Report ID") @PathVariable Long reportId) {

        ReportImageDTO image = reportService.getThumb(reportId);
        return ApiResp.success(image);
    }

    @Operation(summary = "Get report full image", description = "Retrieves the full-size image of a report.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Full image retrieved successfully")
    })
    @GetMapping(GET_REPORT_IMAGE_FULL)
    public ResponseEntity<ApiResp<ReportImageDTO>> getReportImageFull(
            @Parameter(description = "Report ID") @PathVariable Long reportId) {

        ReportImageDTO image = reportService.getFull(reportId);
        return ApiResp.success(image);
    }
}
