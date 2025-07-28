package com.autoria.repository;

import com.autoria.models.ad.CarAd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarAdRepository extends JpaRepository<CarAd, UUID>, JpaSpecificationExecutor<CarAd> {
    Page<CarAd> findBySeller_Id(UUID sellerId, Pageable pageable);
}
