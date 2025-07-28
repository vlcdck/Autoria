package com.autoria.services.ad;

import com.autoria.enums.AdStatus;
import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public interface CarAdService {

    Page<CarAdResponseDto> filterAds(
            AdStatus status,
            UUID brandId,
            UUID modelId,
            Integer yearFrom,
            Integer yearTo,
            Integer mileageFrom,
            Integer mileageTo,
            BigDecimal priceFrom,
            BigDecimal priceTo,
            Pageable pageable
    );

    Page<CarAdResponseDto> findAll(Specification<CarAd> spec, Pageable pageable);

    CarAdResponseDto getAdById(UUID id);

    CarAdResponseDto createAd(CarAdRequestDto dto);

    CarAdResponseDto updateAd(UUID id, CarAdRequestDto dto);

    void deleteAdById(UUID id);

    Page<CarAdResponseDto> getMyAds(Pageable pageable);

    CarAdResponseDto getAnyAdById(UUID id);

    CarAdResponseDto updateAnyAd(UUID id, CarAdRequestDto dto);

    void deleteAnyAd(UUID id);
}
