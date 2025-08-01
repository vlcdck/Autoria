package com.autoria.services.ad.util;

import com.autoria.enums.AdStatus;
import com.autoria.enums.CurrencyCode;
import com.autoria.models.ad.CarAd;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.repository.CarAdRepository;
import com.autoria.repository.CarBrandRepository;
import com.autoria.repository.CarModelRepository;
import com.autoria.repository.DealershipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarAdHelperService {

    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final DealershipRepository dealershipRepository;
    private final CarAdRepository carAdRepository;

    /**
     * Get brand by id or throw an error.
     */
    public CarBrand getBrand(UUID brandId) {
        if (brandId == null) throw new IllegalArgumentException("Brand ID must not be null");
        return carBrandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id " + brandId));
    }

    /**
     * Get model by id or throw an error.
     */
    public CarModel getModel(UUID modelId) {
        if (modelId == null) throw new IllegalArgumentException("Model ID must not be null");
        return carModelRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException("Model not found with id " + modelId));
    }

    /**
     * Get dealership or null if id == null.
     */
    public Dealership getDealership(UUID dealershipId) {
        if (dealershipId == null) return null;
        return dealershipRepository.findById(dealershipId)
                .orElseThrow(() -> new EntityNotFoundException("Dealership not found with id " + dealershipId));
    }

    /**
     * Checking the ad status transition.
     */
    public void validateStatusTransition(AdStatus oldStatus, AdStatus newStatus) {
        if (oldStatus == AdStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot change status from ARCHIVED");
        }
        if (oldStatus == AdStatus.SOLD && newStatus != AdStatus.ARCHIVED) {
            throw new IllegalStateException("After SOLD, only ARCHIVED status allowed");
        }
    }

    /**
     * Find an ad that belongs to the current user, or throw an error.
     */
    public CarAd findAdOwnedByUser(UUID adId, UUID userId) {
        CarAd ad = carAdRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad with id " + adId + " not found"));
        if (!ad.getSeller().getId().equals(userId)) {
            throw new AccessDeniedException("You can operate only on your own ads");
        }
        return ad;
    }

    /**
     * Sets converted prices in UAH, USD and EUR.
     */
    public void setConvertedPrices(CarAd carAd, Map<CurrencyCode, BigDecimal> prices) {
        carAd.setPriceUAH(prices.getOrDefault(CurrencyCode.UAH, BigDecimal.ZERO));
        carAd.setPriceUSD(prices.getOrDefault(CurrencyCode.USD, BigDecimal.ZERO));
        carAd.setPriceEUR(prices.getOrDefault(CurrencyCode.EUR, BigDecimal.ZERO));
        carAd.setExchangeRateSource("PrivatBank API");
        carAd.setExchangeRateDate(LocalDateTime.now());
    }
}
