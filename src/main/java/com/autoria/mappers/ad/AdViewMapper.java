package com.autoria.mappers.ad;

import com.autoria.models.ad.AdView;
import com.autoria.models.ad.dto.AdViewResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AdViewMapper {

    public AdViewResponseDto toDto(AdView adView) {
        AdViewResponseDto adViewResponseDto = new AdViewResponseDto();

        adViewResponseDto.setId(adView.getId());
        adViewResponseDto.setCarAdId(adView.getCarAd() != null ? adView.getCarAd().getId() : null);
        adViewResponseDto.setViewedAt(adView.getViewedAt());
        adViewResponseDto.setRegion(adView.getRegion());
        return adViewResponseDto;
    }
}
