package com.autoria.mappers;

import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CarAdMapper {

    public CarAd toEntity(CarAdRequestDto carAdRequestDto) {
        return CarAd.builder()
                .editAttempts(carAdRequestDto.getEditAttempts())
                .status(carAdRequestDto.getStatus())
                .originalCurrency(carAdRequestDto.getOriginalCurrency())
                .price(carAdRequestDto.getPrice())
                .priceUSD(carAdRequestDto.getPriseUSD())
                .priceEUR(carAdRequestDto.getPriseEUR())
                .priceUAH(carAdRequestDto.getPriseUAH())
                .exchangeRateSource(carAdRequestDto.getExchangeRateSource())
                .exchangeRateDate(carAdRequestDto.getExchangeRateDate())
                .build();
    }

    public CarAdResponseDto toDto(CarAd carAd) {
        CarAdResponseDto carAdResponseDto = new CarAdResponseDto();

        carAdResponseDto.setId(carAd.getId());
        carAdResponseDto.setSellerId(carAd.getSeller() != null ? carAd.getSeller().getId() : null);
        carAdResponseDto.setDealershipId(carAd.getDealership() != null ? carAd.getDealership().getId() : null);
        carAdResponseDto.setBrandId(carAd.getBrand() != null ? carAd.getBrand().getId() : null);
        carAdResponseDto.setModelId(carAd.getModel() != null ? carAd.getModel().getId() : null);
        carAdResponseDto.setStatus(carAd.getStatus());
        carAdResponseDto.setEditAttempts(carAd.getEditAttempts());
        carAdResponseDto.setYear(carAd.getYear());
        carAdResponseDto.setOriginalCurrency(carAd.getOriginalCurrency());
        carAdResponseDto.setPrice(carAd.getPrice());
        carAdResponseDto.setPriceUSD(carAd.getPriceUSD());
        carAdResponseDto.setPriceUAH(carAd.getPriceUAH());
        carAdResponseDto.setPriceEUR(carAd.getPriceEUR());
        carAdResponseDto.setExchangeRateSource(carAd.getExchangeRateSource());
        carAdResponseDto.setExchangeRateDate(carAd.getExchangeRateDate());
        return carAdResponseDto;
    }
}
