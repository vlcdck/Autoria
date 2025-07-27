package com.autoria.services.ad;

import com.autoria.mappers.car.CarAdMapper;
import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import com.autoria.repository.*;
import com.autoria.security.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarAdServiceImpl implements CarAdService {

    private final CarAdRepository carAdRepository;
    private final CarAdMapper carAdMapper;
    private final AppUserRepository appUserRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final DealershipRepository dealershipRepository;

    @Override
    public CarAdResponseDto createAd(CarAdRequestDto dto) {
        AppUser currentUser = SecurityUtil.getCurrentUser();

        // Перевірка існування пов’язаних сутностей
        CarBrand brand = getBrand(dto.getBrandId());
        CarModel model = getModel(dto.getModelId());
        Dealership dealership = getDealership(dto.getDealershipId());

        CarAd carAd = carAdMapper.toEntity(dto, currentUser);
        carAd.setBrand(brand);
        carAd.setModel(model);
        carAd.setDealership(dealership);

        CarAd saved = carAdRepository.save(carAd);
        return carAdMapper.toDto(saved);
    }

    @Override
    public CarAdResponseDto getAdById(UUID id) {
        return carAdRepository.findById(id)
                .map(carAdMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Car ad with id " + id + " not found"));
    }

    @Override
    public CarAdResponseDto updateAd(UUID id, CarAdRequestDto dto) {
        CarAd existingAd = findAdOwnedByCurrentUser(id);

        existingAd.setBrand(getBrand(dto.getBrandId()));
        existingAd.setModel(getModel(dto.getModelId()));
        existingAd.setDealership(getDealership(dto.getDealershipId()));
        existingAd.setYear(dto.getYear());
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
    public List<CarAdResponseDto> getMyAds() {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        return carAdRepository.findBySeller_Id(currentUserId).stream()
                .map(carAdMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAdById(UUID id) {
        CarAd ad = findAdOwnedByCurrentUser(id);
        carAdRepository.deleteById(ad.getId());
    }

    // private methods

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
        return carBrandRepository.findById(brandId)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found with id " + brandId));
    }

    private CarModel getModel(UUID modelId) {
        return carModelRepository.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException("Model not found with id " + modelId));
    }

    private Dealership getDealership(UUID dealershipId) {
        return dealershipRepository.findById(dealershipId)
                .orElseThrow(() -> new EntityNotFoundException("Dealership not found with id " + dealershipId));
    }
}
