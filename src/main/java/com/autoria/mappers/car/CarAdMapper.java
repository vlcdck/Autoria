package com.autoria.mappers.car;

import com.autoria.enums.AdStatus;
import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import com.autoria.repository.AppUserRepository;
import com.autoria.repository.CarBrandRepository;
import com.autoria.repository.CarModelRepository;
import com.autoria.repository.DealershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarAdMapper {

    private final AppUserRepository appUserRepository;
    private final DealershipRepository dealershipRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;

    public CarAd toEntity(CarAdRequestDto dto, AppUser seller) {
        Dealership dealership = dealershipRepository.findById(dto.getDealershipId())
                .orElseThrow(() -> new IllegalArgumentException("Dealership not found"));

        CarBrand brand = carBrandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Car brand not found"));

        CarModel model = carModelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Car model not found"));

        return CarAd.builder()
                .seller(seller)
                .dealership(dealership)
                .brand(brand)
                .model(model)
                .year(dto.getYear())
                .originalCurrency(dto.getOriginalCurrency())
                .price(dto.getPrice())
                .priceUSD(dto.getPriceUSD())
                .priceEUR(dto.getPriceEUR())
                .priceUAH(dto.getPriceUAH())
                .exchangeRateSource(dto.getExchangeRateSource())
                .exchangeRateDate(dto.getExchangeRateDate())
                .editAttempts(0)
                .status(AdStatus.PENDING)
                .build();
    }

    public CarAdResponseDto toDto(CarAd ad) {
        CarAdResponseDto dto = new CarAdResponseDto();

        dto.setId(ad.getId());
        dto.setSellerId(ad.getSeller() != null ? ad.getSeller().getId() : null);
        dto.setDealershipId(ad.getDealership() != null ? ad.getDealership().getId() : null);
        dto.setBrandId(ad.getBrand() != null ? ad.getBrand().getId() : null);
        dto.setModelId(ad.getModel() != null ? ad.getModel().getId() : null);
        dto.setStatus(ad.getStatus());
        dto.setEditAttempts(ad.getEditAttempts());
        dto.setYear(ad.getYear());
        dto.setOriginalCurrency(ad.getOriginalCurrency());
        dto.setPrice(ad.getPrice());
        dto.setPriceUSD(ad.getPriceUSD());
        dto.setPriceUAH(ad.getPriceUAH());
        dto.setPriceEUR(ad.getPriceEUR());
        dto.setExchangeRateSource(ad.getExchangeRateSource());
        dto.setExchangeRateDate(ad.getExchangeRateDate());

        return dto;
    }
}
