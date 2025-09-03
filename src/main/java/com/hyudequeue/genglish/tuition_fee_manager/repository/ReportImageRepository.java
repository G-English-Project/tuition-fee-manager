package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.ReportImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {
    Optional<ReportImage> findByReport_ReportId(Long reportId);
}
