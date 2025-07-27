package com.autoria.repository;

import com.autoria.models.ad.CarAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarAdRepository extends JpaRepository<CarAd, UUID> {
    List<CarAd> findBySeller_Id(UUID sellerId);

}
