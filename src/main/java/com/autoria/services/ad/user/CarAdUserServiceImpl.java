package com.autoria.services.ad.user;

import com.autoria.enums.AdStatus;
import com.autoria.enums.ModerationResult;
import com.autoria.mappers.car.CarAdMapper;
import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.models.user.AppUser;
import com.autoria.repository.CarAdRepository;
import com.autoria.repository.specifications.CarAdSpecification;
import com.autoria.security.util.SecurityUtil;
import com.autoria.services.ad.util.CarAdHelperService;
import com.autoria.services.moderation.OpenAiModerationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CarAdUserServiceImpl implements CarAdUserService {

    private final CarAdRepository carAdRepository;
    private final CarAdMapper carAdMapper;
    private final OpenAiModerationService openAiModerationService;
    private final CarAdHelperService helper;

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
            String descriptionContains,
            Pageable pageable) {

        boolean isAuthenticated = SecurityUtil.isAuthenticated();
        boolean isManager = SecurityUtil.hasRole("MANAGER");

        if (!isAuthenticated) {
            status = AdStatus.ACTIVE;
        }
        if (isAuthenticated && !isManager && status == null) {
            status = AdStatus.ACTIVE;
        }

        Specification<CarAd> spec = CarAdSpecification.filterBy(
                status, brandId, modelId, yearFrom, yearTo, mileageFrom, mileageTo, priceFrom, priceTo, descriptionContains);

        return carAdRepository.findAll(spec, pageable)
                .map(carAdMapper::toDto);
    }

    @Override
    public CarAdResponseDto getAdById(UUID id) {
        CarAd ad = carAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car ad with id " + id + " not found"));

        if (!SecurityUtil.isAuthenticated() && ad.getStatus() != AdStatus.ACTIVE) {
            throw new AccessDeniedException("Only active ads are visible");
        }
        return carAdMapper.toDto(ad);
    }

    @Override
    public CarAdResponseDto createAd(CarAdRequestDto dto) {
        AppUser currentUser = SecurityUtil.getCurrentUser();
        var brand = helper.getBrand(dto.getBrandId());
        var model = helper.getModel(dto.getModelId());
        var dealership = helper.getDealership(dto.getDealershipId());

        CarAd carAd = carAdMapper.toEntity(dto, currentUser, dealership, brand, model);

        ModerationResult moderationResult = openAiModerationService.checkContent(dto.getDescription());
        log.debug("Moderation result: {}", moderationResult);
        switch (moderationResult) {
            case SAFE -> {
                carAd.setStatus(AdStatus.ACTIVE);
                carAd.setEditAttempts(0);
            }
            case FLAGGED -> {
                carAd.setStatus(AdStatus.REJECTED);
                carAd.setEditAttempts(1);
            }
            case QUOTA_EXCEEDED, ERROR -> {
                log.warn("OpenAI moderation issue ({}), creating ad with status PENDING", moderationResult);
                carAd.setStatus(AdStatus.UNDER_MODERATION);
                carAd.setEditAttempts(0);
            }
            default -> {
                carAd.setStatus(AdStatus.UNDER_MODERATION);
                carAd.setEditAttempts(0);
            }
        }

        CarAd saved = carAdRepository.save(carAd);
        log.info("User {} created ad {} with status {}", currentUser.getId(), saved.getId(), saved.getStatus());
        return carAdMapper.toDto(saved);
    }

    @Override
    public CarAdResponseDto updateAd(UUID id, CarAdRequestDto dto) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        CarAd existingAd = helper.findAdOwnedByUser(id, currentUserId);

        if (existingAd.getEditAttempts() != null && existingAd.getEditAttempts() >= 3) {
            boolean isModerator = SecurityUtil.hasRole("ADMIN") || SecurityUtil.hasRole("MANAGER");
            if (!isModerator) {
                log.warn("User {} attempted to edit locked ad {}", currentUserId, id);
                throw new AccessDeniedException("Оголошення заблоковане для редагування. Очікує перевірку модератором.");
            }
        }

        helper.validateStatusTransition(existingAd.getStatus(), dto.getStatus());

        ModerationResult moderationResult = openAiModerationService.checkContent(dto.getDescription());
        log.info("Moderation result: {}", moderationResult);

        if (moderationResult == ModerationResult.FLAGGED) {
            int attempts = existingAd.getEditAttempts() == null ? 0 : existingAd.getEditAttempts();
            attempts++;
            existingAd.setEditAttempts(attempts);

            if (attempts >= 3) {
                existingAd.setStatus(AdStatus.UNDER_MODERATION);
            } else {
                existingAd.setStatus(AdStatus.REJECTED);
            }

            carAdRepository.save(existingAd);
            log.warn("Description failed moderation. Attempts: {}. Current status: {}", attempts, existingAd.getStatus());
            throw new IllegalArgumentException("Опис містить заборонений контент. Редагування обмежено.");
        } else if (moderationResult == ModerationResult.QUOTA_EXCEEDED || moderationResult == ModerationResult.ERROR) {
            log.warn("OpenAI moderation issue: {}. Allowing update with status PENDING.", moderationResult);

            int attempts = existingAd.getEditAttempts() == null ? 0 : existingAd.getEditAttempts();
            attempts++;
            existingAd.setEditAttempts(attempts);

            if (attempts >= 3) {
                existingAd.setStatus(AdStatus.UNDER_MODERATION);
            } else {
                existingAd.setStatus(AdStatus.PENDING);
            }
        } else {
            existingAd.setEditAttempts(0);
            existingAd.setStatus(AdStatus.ACTIVE);
        }

        updateCarAdFields(existingAd, dto);
        CarAd saved = carAdRepository.save(existingAd);

        log.info("User {} updated ad {} with status {}", existingAd.getSeller().getId(), saved.getId(), saved.getStatus());
        return carAdMapper.toDto(saved);
    }

    @Override
    public Page<CarAdResponseDto> getMyAds(
            AdStatus status,
            UUID brandId,
            UUID modelId,
            Integer yearFrom,
            Integer yearTo,
            Integer mileageFrom,
            Integer mileageTo,
            BigDecimal priceFrom,
            BigDecimal priceTo,
            String descriptionContains,
            Pageable pageable) {

        UUID currentUserId = SecurityUtil.getCurrentUserId();

        Specification<CarAd> filterSpec = CarAdSpecification.filterBy(
                status, brandId, modelId, yearFrom, yearTo, mileageFrom, mileageTo, priceFrom, priceTo, descriptionContains);

        Specification<CarAd> userSpec = CarAdSpecification.ownedByUser(currentUserId);

        Specification<CarAd> spec = filterSpec.and(userSpec);

        return carAdRepository.findAll(spec, pageable)
                .map(carAdMapper::toDto);
    }

    @Override
    public CarAdResponseDto getMyAdById(UUID id) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        CarAd ad = helper.findAdOwnedByUser(id, currentUserId);
        return carAdMapper.toDto(ad);
    }

    @Override
    public void deleteAdById(UUID id) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        CarAd ad = helper.findAdOwnedByUser(id, currentUserId);
        carAdRepository.deleteById(ad.getId());
        log.info("User {} deleted ad {}", ad.getSeller().getId(), ad.getId());
    }

    private void updateCarAdFields(CarAd ad, CarAdRequestDto dto) {
        ad.setBrand(helper.getBrand(dto.getBrandId()));
        ad.setModel(helper.getModel(dto.getModelId()));
        ad.setDealership(helper.getDealership(dto.getDealershipId()));
        ad.setYear(dto.getYear());
        ad.setMileage(dto.getMileage());
        ad.setFuelType(dto.getFuelType());
        ad.setOwnersCount(dto.getOwnersCount());
        ad.setPhotos(dto.getPhotos());
        ad.setOriginalCurrency(dto.getOriginalCurrency());
        ad.setPrice(dto.getPrice());
        ad.setPriceUSD(dto.getPriceUSD());
        ad.setPriceEUR(dto.getPriceEUR());
        ad.setPriceUAH(dto.getPriceUAH());
        ad.setExchangeRateSource(dto.getExchangeRateSource());
        ad.setExchangeRateDate(dto.getExchangeRateDate());
        ad.setDescription(dto.getDescription());
    }
}
