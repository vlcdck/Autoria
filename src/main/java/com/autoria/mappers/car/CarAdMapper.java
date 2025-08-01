package com.autoria.mappers.car;

import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.models.ad.dto.SimpleEntityDto;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import com.autoria.models.user.dto.SimpleAppUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarAdMapper {

    public CarAdResponseDto toDto(CarAd ad) {
        if (ad == null) return null;

        CarAdResponseDto dto = new CarAdResponseDto();
        dto.setId(ad.getId());
        dto.setStatus(ad.getStatus());
        dto.setEditAttempts(ad.getEditAttempts());
        dto.setYear(ad.getYear());
        dto.setMileage(ad.getMileage());
        dto.setFuelType(ad.getFuelType());
        dto.setOwnersCount(ad.getOwnersCount());
        dto.setPhotos(ad.getPhotos());
        dto.setDescription(ad.getDescription());
        dto.setOriginalCurrency(ad.getOriginalCurrency());
        dto.setPrice(ad.getPrice());
        dto.setPriceUSD(ad.getPriceUSD());
        dto.setPriceUAH(ad.getPriceUAH());
        dto.setPriceEUR(ad.getPriceEUR());
        dto.setExchangeRateSource(ad.getExchangeRateSource());
        dto.setExchangeRateDate(ad.getExchangeRateDate());

        if (ad.getSeller() != null) {
            AppUser seller = ad.getSeller();
            dto.setSeller(new SimpleAppUserDto(seller.getId(), seller.getFirstName(), seller.getLastName(), seller.getEmail(), seller.getPhoneNumber()));

        }
        if (ad.getDealership() != null) {
            dto.setDealership(new SimpleEntityDto(ad.getDealership().getId(), ad.getDealership().getName()));
        }
        if (ad.getBrand() != null) {
            dto.setBrand(new SimpleEntityDto(ad.getBrand().getId(), ad.getBrand().getName()));
        }
        if (ad.getModel() != null) {
            dto.setModel(new SimpleEntityDto(ad.getModel().getId(), ad.getModel().getModel()));
        }

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
                .description(dto.getDescription())
                .photos(dto.getPhotos())
                .originalCurrency(dto.getOriginalCurrency())
                .price(dto.getPrice())
                .build();
    }
}
