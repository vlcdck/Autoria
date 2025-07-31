package com.autoria.services.ad.admin;

import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;

import java.util.UUID;

public interface CarAdAdminService {

    CarAdResponseDto getAnyAdById(UUID id);

    CarAdResponseDto updateAnyAd(UUID id, CarAdRequestDto dto);

    void deleteAnyAd(UUID id);
}