// service/services/ReportService.java
package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request.CreateReportRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request.UpdateReportRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.response.ReportImageDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.response.ReportResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ReportService {

    ReportResponseDTO create(CreateReportRequest req);

    ReportResponseDTO update(Long reportId, UpdateReportRequest req);

    void delete(Long reportId);

    ReportResponseDTO getById(Long reportId);

    Page<ReportResponseDTO> listByStudent(Long studentId, Pageable pageable);

    Page<ReportResponseDTO> listByClass(Long classId, Pageable pageable);

    Page<ReportResponseDTO> listByStudentInRange(Long studentId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<ReportResponseDTO> listByClassInRange(Long classId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Ảnh (endpoint riêng)
    ReportImageDTO getThumb(Long reportId);

    ReportImageDTO getFull(Long reportId);
}
