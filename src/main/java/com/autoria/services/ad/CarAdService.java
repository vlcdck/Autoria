package com.autoria.services.ad;

import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;

import java.util.List;
import java.util.UUID;

public interface CarAdService {
    CarAdResponseDto createAd(CarAdRequestDto carAdRequestDto);

    CarAdResponseDto getAdById(UUID id);

    CarAdResponseDto updateAd(UUID id, CarAdRequestDto carAdRequestDto);

    List<CarAdResponseDto> getMyAds();

    void deleteAdById(UUID id);
}
