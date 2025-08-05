package com.autoria.mappers.car;

import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdAnalyticsDto;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.models.ad.dto.SimpleEntityDto;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import com.autoria.models.user.dto.SimpleAppUserDto;
import com.autoria.repository.AdViewRepository;
import com.autoria.repository.CarAdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CarAdMapper {

    private final CarAdRepository carAdRepository;
    private final AdViewRepository adViewRepository;

    public CarAdResponseDto toDto(CarAd ad) {
        return toDto(ad, false); // базовий варіант
    }

    public CarAdResponseDto toDto(CarAd ad, boolean isPremium) {
        if (ad == null) return null;

        CarAdResponseDto dto = new CarAdResponseDto();
        dto.setId(ad.getId());
        dto.setStatus(ad.getStatus());
        dto.setEditAttempts(ad.getEditAttempts());
        dto.setYear(ad.getYear());
        dto.setMileage(ad.getMileage());
        dto.setFuelType(ad.getFuelType());
        dto.setOwnersCount(ad.getOwnersCount());
        dto.setRegion(ad.getRegion());
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
            dto.setSeller(new SimpleAppUserDto(
                    seller.getId(),
                    seller.getFirstName(),
                    seller.getLastName(),
                    seller.getEmail(),
                    seller.getPhoneNumber()));
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

        // Додаємо аналітику тільки якщо користувач Premium
        if (isPremium) {
            LocalDateTime now = LocalDateTime.now();
            UUID adId = ad.getId();

            long totalViews = adViewRepository.countByCarAdId(adId);
            long dailyViews = adViewRepository.countByCarAdIdAndViewedAtAfter(adId, now.minusDays(1));
            long weeklyViews = adViewRepository.countByCarAdIdAndViewedAtAfter(adId, now.minusWeeks(1));
            long monthlyViews = adViewRepository.countByCarAdIdAndViewedAtAfter(adId, now.minusMonths(1));

            BigDecimal avgPriceRegion = carAdRepository.findAvgPriceByRegion(ad.getRegion());
            BigDecimal avgPriceUkraine = carAdRepository.findAvgPriceUkraine();

            dto.setAnalytics(CarAdAnalyticsDto.builder()
                    .totalViews(totalViews)
                    .dailyViews(dailyViews)
                    .weeklyViews(weeklyViews)
                    .monthlyViews(monthlyViews)
                    .avgPriceRegion(avgPriceRegion)
                    .avgPriceUkraine(avgPriceUkraine)
                    .build());
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
                .region(dto.getRegion())
                .fuelType(dto.getFuelType())
                .ownersCount(dto.getOwnersCount())
                .description(dto.getDescription())
                .photos(dto.getPhotos())
                .originalCurrency(dto.getOriginalCurrency())
                .price(dto.getPrice())
                .build();
    }
}
