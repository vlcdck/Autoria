package com.autoria.services.ad;

import com.autoria.enums.AdStatus;
import com.autoria.mappers.car.CarAdMapper;
import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import com.autoria.repository.CarAdRepository;
import com.autoria.repository.CarBrandRepository;
import com.autoria.repository.CarModelRepository;
import com.autoria.repository.DealershipRepository;
import com.autoria.repository.specifications.CarAdSpecification;
import com.autoria.security.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarAdServiceImpl implements CarAdService {

    private final CarAdRepository carAdRepository;
    private final CarAdMapper carAdMapper;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final DealershipRepository dealershipRepository;

    @Override
    public Page<CarAdResponseDto> filterAds(
            AdStatus status,
            UUID brandId,
            UUID modelId,
            Integer yearFrom,
            Integer yearTo,
            Integer mileageFrom,
            Integer mileageTo,
            BigDecimal priceFrom,
            BigDecimal priceTo,
            Pageable pageable
    ) {
        Specification<CarAd> spec = CarAdSpecification.filterBy(
                status, brandId, modelId, yearFrom, yearTo, mileageFrom, mileageTo, priceFrom, priceTo
        );

        return carAdRepository.findAll(spec, pageable)
                .map(carAdMapper::toDto);
    }

    @Override
    public Page<CarAdResponseDto> findAll(Specification<CarAd> spec, Pageable pageable) {
        return carAdRepository.findAll(spec, pageable)
                .map(carAdMapper::toDto);
    }

    @Override
    public CarAdResponseDto getAdById(UUID id) {
        return carAdRepository.findById(id)
                .map(carAdMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Car ad with id " + id + " not found"));
    }

    @Override
    public CarAdResponseDto createAd(CarAdRequestDto dto) {
        AppUser currentUser = SecurityUtil.getCurrentUser();
        CarBrand brand = getBrand(dto.getBrandId());
        CarModel model = getModel(dto.getModelId());
        Dealership dealership = getDealership(dto.getDealershipId());

        CarAd carAd = carAdMapper.toEntity(dto, currentUser, dealership, brand, model);
        CarAd saved = carAdRepository.save(carAd);
        return carAdMapper.toDto(saved);
    }

    @Override
    public CarAdResponseDto updateAd(UUID id, CarAdRequestDto dto) {
        CarAd existingAd = findAdOwnedByCurrentUser(id);
        validateStatusTransition(existingAd.getStatus(), dto.getStatus());

        existingAd.setStatus(dto.getStatus());
        existingAd.setBrand(getBrand(dto.getBrandId()));
        existingAd.setModel(getModel(dto.getModelId()));
        existingAd.setDealership(getDealership(dto.getDealershipId()));
        existingAd.setYear(dto.getYear());
        existingAd.setMileage(dto.getMileage());
        existingAd.setFuelType(dto.getFuelType());
        existingAd.setOwnersCount(dto.getOwnersCount());
        existingAd.setPhotos(dto.getPhotos());
        existingAd.setOriginalCurrency(dto.getOriginalCurrency());
        existingAd.setPrice(dto.getPrice());
        existingAd.setPriceUSD(dto.getPriceUSD());
        existingAd.setPriceEUR(dto.getPriceEUR());
        existingAd.setPriceUAH(dto.getPriceUAH());
        existingAd.setExchangeRateSource(dto.getExchangeRateSource());
        existingAd.setExchangeRateDate(dto.getExchangeRateDate());

        CarAd saved = carAdRepository.save(existingAd);
        return carAdMapper.toDto(saved);
    }

    @Override
    public Page<CarAdResponseDto> getMyAds(Pageable pageable) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        return carAdRepository.findBySeller_Id(currentUserId, pageable)
                .map(carAdMapper::toDto);
    }

    @Override
    public void deleteAdById(UUID id) {
        CarAd ad = findAdOwnedByCurrentUser(id);
        carAdRepository.deleteById(ad.getId());
    }

    // --- Admin methods ---

    @Override
    public CarAdResponseDto getAnyAdById(UUID id) {
        return carAdRepository.findById(id)
                .map(carAdMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Car ad with id " + id + " not found"));
    }

    @Override
    public CarAdResponseDto updateAnyAd(UUID id, CarAdRequestDto dto) {
        CarAd existingAd = carAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car ad with id " + id + " not found"));

        existingAd.setStatus(dto.getStatus());
        existingAd.setBrand(getBrand(dto.getBrandId()));
        existingAd.setModel(getModel(dto.getModelId()));
        existingAd.setDealership(getDealership(dto.getDealershipId()));
        existingAd.setYear(dto.getYear());
        existingAd.setMileage(dto.getMileage());
        existingAd.setFuelType(dto.getFuelType());
        existingAd.setOwnersCount(dto.getOwnersCount());
        existingAd.setPhotos(dto.getPhotos());
        existingAd.setOriginalCurrency(dto.getOriginalCurrency());
        existingAd.setPrice(dto.getPrice());
        existingAd.setPriceUSD(dto.getPriceUSD());
        existingAd.setPriceEUR(dto.getPriceEUR());
        existingAd.setPriceUAH(dto.getPriceUAH());
        existingAd.setExchangeRateSource(dto.getExchangeRateSource());
        existingAd.setExchangeRateDate(dto.getExchangeRateDate());

        CarAd saved = carAdRepository.save(existingAd);
        return carAdMapper.toDto(saved);
    }

    @Override
    public void deleteAnyAd(UUID id) {
        if (!carAdRepository.existsById(id)) {
            throw new EntityNotFoundException("Car ad with id " + id + " not found");
        }
        carAdRepository.deleteById(id);
    }

    // ======================== private methods =============================

    private CarAd findAdOwnedByCurrentUser(UUID adId) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        CarAd ad = carAdRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad with id " + adId + " not found"));

        if (!ad.getSeller().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You can operate only on your own ads");
        }
        return ad;
    }

    private CarBrand getBrand(UUID brandId) {
        if (brandId == null) throw new IllegalArgumentException("Brand ID must not be null");
        return carBrandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id " + brandId));
    }

    private CarModel getModel(UUID modelId) {
        if (modelId == null) throw new IllegalArgumentException("Model ID must not be null");
        return carModelRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException("Model not found with id " + modelId));
    }

    private Dealership getDealership(UUID dealershipId) {
        if (dealershipId == null) return null;
        return dealershipRepository.findById(dealershipId)
                .orElseThrow(() -> new EntityNotFoundException("Dealership not found with id " + dealershipId));
    }

    private void validateStatusTransition(AdStatus oldStatus, AdStatus newStatus) {
        if (oldStatus == AdStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot change status from ARCHIVED");
        }
        if (oldStatus == AdStatus.SOLD && newStatus != AdStatus.ARCHIVED) {
            throw new IllegalStateException("After SOLD, only ARCHIVED status allowed");
        }
        // Add other status rules if needed
    }
}
