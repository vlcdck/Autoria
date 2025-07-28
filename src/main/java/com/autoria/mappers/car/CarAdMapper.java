package com.autoria.mappers.car;

import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarAdMapper {

    public CarAdResponseDto toDto(CarAd ad) {
        if (ad == null) return null;

        CarAdResponseDto dto = new CarAdResponseDto();
        dto.setId(ad.getId());
        dto.setSellerId(ad.getSeller() != null ? ad.getSeller().getId() : null);
        dto.setStatus(ad.getStatus());
        dto.setEditAttempts(ad.getEditAttempts());
        dto.setDealershipId(ad.getDealership() != null ? ad.getDealership().getId() : null);
        dto.setBrandId(ad.getBrand().getId());
        dto.setModelId(ad.getModel().getId());
        dto.setYear(ad.getYear());
        dto.setMileage(ad.getMileage());
        dto.setFuelType(ad.getFuelType());
        dto.setOwnersCount(ad.getOwnersCount());
        dto.setPhotos(ad.getPhotos());
        dto.setOriginalCurrency(ad.getOriginalCurrency());
        dto.setPrice(ad.getPrice());
        dto.setPriceUSD(ad.getPriceUSD());
        dto.setPriceUAH(ad.getPriceUAH());
        dto.setPriceEUR(ad.getPriceEUR());
        dto.setExchangeRateSource(ad.getExchangeRateSource());
        dto.setExchangeRateDate(ad.getExchangeRateDate());

        return dto;
    }

    public CarAd toEntity(CarAdRequestDto dto, AppUser seller, Dealership dealership, CarBrand brand, CarModel model) {
        if (dto == null) return null;

        return CarAd.builder()
                .seller(seller)
                .status(dto.getStatus())
                .editAttempts(dto.getEditAttempts())
                .dealership(dealership)
                .brand(brand)
                .model(model)
                .year(dto.getYear())
                .mileage(dto.getMileage())
                .fuelType(dto.getFuelType())
                .ownersCount(dto.getOwnersCount())
                .photos(dto.getPhotos())
                .originalCurrency(dto.getOriginalCurrency())
                .price(dto.getPrice())
                .priceUSD(dto.getPriceUSD())
                .priceUAH(dto.getPriceUAH())
                .priceEUR(dto.getPriceEUR())
                .exchangeRateSource(dto.getExchangeRateSource())
                .exchangeRateDate(dto.getExchangeRateDate())
                .build();
    }
}
