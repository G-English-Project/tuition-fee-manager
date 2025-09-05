package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request.CreateReportRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.request.UpdateReportRequest;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.response.ReportImageDTO;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Report.response.ReportResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Classes;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Report;
import com.hyudequeue.genglish.tuition_fee_manager.entities.ReportImage;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ClassRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ReportImageRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.ReportRepository;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
    private final UserRepository userRepository;
    private final ClassRepository classesRepository;

    @Override
    public ReportResponseDTO create(CreateReportRequest req) {
        User student = userRepository.findById(req.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Student not found"));
        Classes clazz = classesRepository.findById(req.getClassId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Class not found"));

        Report report = Report.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .student(student)
                .classRoom(clazz)
                .point(req.getPoint())
                .hasImage(false)
                .createdAt(LocalDateTime.now())
                .build();
        report = reportRepository.save(report);

        // Nếu có gửi ảnh kèm theo
        if (hasAnyImage(req.getImageThumbBase64(), req.getImageBase64())) {
            upsertImage(report, req.getImageThumbBase64(), req.getImageBase64(), req.getMimeType());
        }

        return toDto(report);
    }

    @Override
    public ReportResponseDTO update(Long reportId, UpdateReportRequest req) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Report not found"));

        if (req.getTitle() != null) report.setTitle(req.getTitle());
        if (req.getContent() != null) report.setContent(req.getContent());
        if (req.getPoint() != null) report.setPoint(req.getPoint());

        if (req.getImageThumbBase64() != null || req.getImageBase64() != null || req.getMimeType() != null) {
            if (isDeleteImage(req)) {
                Optional.ofNullable(report.getImage()).ifPresent(reportImageRepository::delete);
                report.setImage(null);
                report.setHasImage(false);
            } else if (hasAnyImage(req.getImageThumbBase64(), req.getImageBase64())) {
                upsertImage(report, req.getImageThumbBase64(), req.getImageBase64(), req.getMimeType());
            }
        }
        return toDto(reportRepository.save(report));
    }

    @Override
    public void delete(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Report not found"));
        // orphanRemoval=true sẽ xóa ảnh nếu có
        reportRepository.delete(report);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponseDTO getById(Long reportId) {
        Report r = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Report not found"));
        return toDto(r);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponseDTO> listByStudent(Long studentId, Pageable pageable) {
        return reportRepository.findByStudent_UserId(studentId, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponseDTO> listByClass(Long classId, Pageable pageable) {
        return reportRepository.findByClassRoom_ClassId(classId, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponseDTO> listByStudentInRange(Long studentId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return reportRepository.findByStudent_UserIdAndCreatedAtBetween(studentId, from, to, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportResponseDTO> listByClassInRange(Long classId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return reportRepository.findByClassRoom_ClassIdAndCreatedAtBetween(classId, from, to, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportImageDTO getThumb(Long reportId) {
        ReportImage img = reportImageRepository.findByReport_ReportId(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Image not found"));
        if (img.getImageThumbBase64() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "No thumbnail");
        }
        String dataUri = toDataUri(img.getMimeType(), img.getImageThumbBase64());
        return new ReportImageDTO(img.getMimeType(), dataUri, eTagOf(img.getImageThumbBase64()));
    }

    @Override
    @Transactional(readOnly = true)
    public ReportImageDTO getFull(Long reportId) {
        ReportImage img = reportImageRepository.findByReport_ReportId(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Image not found"));
        if (img.getImageBase64() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "No image");
        }
        String dataUri = toDataUri(img.getMimeType(), img.getImageBase64());
        return new ReportImageDTO(img.getMimeType(), dataUri, eTagOf(img.getImageBase64()));
    }

    // ========== Helpers ==========
    private ReportResponseDTO toDto(Report r) {
        return ReportResponseDTO.builder()
                .reportId(r.getReportId())
                .title(r.getTitle())
                .content(r.getContent())
                .hasImage(Boolean.TRUE.equals(r.getHasImage()))
                .studentId(r.getStudent().getUserId())
                .studentName(r.getStudent().getFullName())
                .classId(r.getClassRoom().getClassId())
                .className(r.getClassRoom().getClassName())
                .point(r.getPoint())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private boolean hasAnyImage(String thumb, String full) {
        return (thumb != null && !thumb.isEmpty()) || (full != null && !full.isEmpty());
    }

    private boolean isDeleteImage(UpdateReportRequest req) {
        boolean thumbDelete = req.getImageThumbBase64() != null && req.getImageThumbBase64().isEmpty();
        boolean fullDelete  = req.getImageBase64() != null && req.getImageBase64().isEmpty();
        boolean mimeDelete  = req.getMimeType() != null && req.getMimeType().isEmpty();
        return thumbDelete || fullDelete || mimeDelete;
    }

    private void upsertImage(Report report, String thumbB64, String fullB64, String mime) {
        ReportImage img = Optional.ofNullable(report.getImage()).orElseGet(ReportImage::new);
        img.setReport(report);
        if (mime != null && !mime.isEmpty()) img.setMimeType(mime);
        if (thumbB64 != null) img.setImageThumbBase64(stripPrefix(thumbB64));
        if (fullB64 != null) img.setImageBase64(stripPrefix(fullB64));

        report.setImage(img);
        report.setHasImage(true);

        reportRepository.save(report);
    }

    private String stripPrefix(String b64) {
        if (b64 == null) return null;
        int i = b64.indexOf("base64,");
        return i >= 0 ? b64.substring(i + 7) : b64;
    }

    private String toDataUri(String mime, String base64) {
        String clean = stripPrefix(base64);
        String m = (mime == null || mime.isEmpty()) ? "image/webp" : mime;
        return "data:%s;base64,%s".formatted(m, clean);
    }

    private String eTagOf(String base64) {
        String s = base64 == null ? "" : stripPrefix(base64);
        return generateSha256Hex(s);
    }

    private String generateSha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }
}
