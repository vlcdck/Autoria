package com.autoria.repository;

import com.autoria.models.ad.CarAd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface CarAdRepository extends JpaRepository<CarAd, UUID>, JpaSpecificationExecutor<CarAd> {

    @Query("SELECT AVG(c.price) FROM CarAd c WHERE c.region = :region AND c.status = 'ACTIVE'")
    BigDecimal findAvgPriceByRegion(String region);

    @Query("SELECT AVG(c.price) FROM CarAd c WHERE c.status = 'ACTIVE'")
    BigDecimal findAvgPriceUkraine();

}
