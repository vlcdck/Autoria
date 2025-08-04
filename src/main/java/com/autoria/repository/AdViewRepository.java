package com.autoria.repository;

import com.autoria.models.ad.AdView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AdViewRepository extends JpaRepository<AdView, UUID> {

    long countByCarAdId(UUID carAdId);

    long countByCarAdIdAndViewedAtAfter(UUID carAdId, LocalDateTime after);
}
