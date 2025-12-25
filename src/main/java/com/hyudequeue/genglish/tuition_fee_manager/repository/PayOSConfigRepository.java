package com.hyudequeue.genglish.tuition_fee_manager.repository;

import com.hyudequeue.genglish.tuition_fee_manager.entities.PayOSConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayOSConfigRepository extends JpaRepository<PayOSConfig, Long> {
    Optional<PayOSConfig> findFirstByOrderByIdAsc();
}

