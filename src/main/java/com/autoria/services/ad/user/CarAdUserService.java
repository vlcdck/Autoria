package com.autoria.services.ad.user;

import com.autoria.enums.AdStatus;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;
public interface CarAdUserService {

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
            String descriptionContains,
            Pageable pageable
    );

    CarAdResponseDto getAdById(UUID id);

    CarAdResponseDto createAd(CarAdRequestDto dto);

    CarAdResponseDto updateAd(UUID id, CarAdRequestDto dto);

    Page<CarAdResponseDto> getMyAds(
            AdStatus status,
            UUID brandId,
            UUID modelId,
            Integer yearFrom,
            Integer yearTo,
            Integer mileageFrom,
            Integer mileageTo,
            BigDecimal priceFrom,
            BigDecimal priceTo,
            String descriptionContains,
            Pageable pageable
    );

    CarAdResponseDto getMyAdById(UUID id);

    void deleteAdById(UUID id);
}