package com.autoria.services.ad.user;

import com.autoria.enums.AdStatus;
import com.autoria.enums.CurrencyCode;
import com.autoria.enums.ModerationResult;
import com.autoria.mappers.car.CarAdMapper;
import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import com.autoria.repository.CarAdRepository;
import com.autoria.repository.specifications.CarAdSpecification;
import com.autoria.security.util.SecurityUtil;
import com.autoria.services.ad.util.CarAdHelperService;
import com.autoria.services.currency.CurrencyRateService;
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
import java.util.Map;
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
    private final CurrencyRateService currencyRateService;

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

        final boolean isAuthenticated = SecurityUtil.isAuthenticated();
        final boolean isManager = SecurityUtil.hasRole("MANAGER");
        final UUID currentUserId = isAuthenticated ? SecurityUtil.getCurrentUserId() : null;
        final boolean isPremium = isAuthenticated && isCurrentUserPremium();

        // Якщо користувач неавторизований або авторизований, але не менеджер,
        // і статус не вказаний — показуємо тільки ACTIVE
        if (!isAuthenticated || (isAuthenticated && !isManager && status == null)) {
            status = AdStatus.ACTIVE;
        }

        final Specification<CarAd> spec = CarAdSpecification.filterBy(
                status, brandId, modelId, yearFrom, yearTo, mileageFrom, mileageTo, priceFrom, priceTo, descriptionContains);

        return carAdRepository.findAll(spec, pageable)
                .map(ad -> {
                    final boolean isOwner = isAuthenticated && ad.getSeller().getId().equals(currentUserId);
                    return carAdMapper.toDto(ad, isOwner || isPremium);
                });
    }

    @Override
    public CarAdResponseDto getAdById(UUID id) {
        final CarAd ad = carAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car ad with id " + id + " not found"));

        if (!SecurityUtil.isAuthenticated() && ad.getStatus() != AdStatus.ACTIVE) {
            throw new AccessDeniedException("Only active ads are visible");
        }

        final boolean isOwner = SecurityUtil.isAuthenticated() && ad.getSeller().getId().equals(SecurityUtil.getCurrentUserId());
        final boolean isPremium = isCurrentUserPremium();

        return carAdMapper.toDto(ad, isOwner || isPremium);
    }

    @Override
    public CarAdResponseDto createAd(CarAdRequestDto dto) {
        final AppUser currentUser = SecurityUtil.getCurrentUser();
        final CarBrand brand = helper.getBrand(dto.getBrandId());
        final CarModel model = helper.getModel(dto.getModelId());
        final Dealership dealership = helper.getDealership(dto.getDealershipId());

        final Map<CurrencyCode, BigDecimal> convertedPrices =
                currencyRateService.convertToAll(dto.getOriginalCurrency(), dto.getPrice());

        final CarAd carAd = carAdMapper.toEntity(dto, currentUser, dealership, brand, model);
        helper.setConvertedPrices(carAd, convertedPrices);

        final ModerationResult moderationResult = openAiModerationService.checkContent(dto.getDescription());
        log.debug("Moderation result: {}", moderationResult);

        if (moderationResult == ModerationResult.SAFE) {
            carAd.setStatus(AdStatus.ACTIVE);
            carAd.setEditAttempts(0);
        } else if (moderationResult == ModerationResult.FLAGGED) {
            carAd.setStatus(AdStatus.REJECTED);
            carAd.setEditAttempts(1);
        } else {
            log.warn("OpenAI moderation issue ({}), creating ad with status PENDING", moderationResult);
            carAd.setStatus(AdStatus.UNDER_MODERATION);
            carAd.setEditAttempts(0);
        }

        final CarAd saved = carAdRepository.save(carAd);
        log.info("User {} created ad {} with status {}", currentUser.getId(), saved.getId(), saved.getStatus());

        return carAdMapper.toDto(saved, isCurrentUserPremium());
    }

    @Override
    public CarAdResponseDto updateAd(UUID id, CarAdRequestDto dto) {
        final UUID currentUserId = SecurityUtil.getCurrentUserId();
        final CarAd existingAd = helper.findAdOwnedByUser(id, currentUserId);

        final boolean isModerator = SecurityUtil.hasRole("ADMIN") || SecurityUtil.hasRole("MANAGER");
        if (existingAd.getEditAttempts() != null && existingAd.getEditAttempts() >= 3 && !isModerator) {
            log.warn("User {} attempted to edit locked ad {}", currentUserId, id);
            throw new AccessDeniedException("Оголошення заблоковане для редагування. Очікує перевірку модератором.");
        }

        helper.validateStatusTransition(existingAd.getStatus(), dto.getStatus());

        final ModerationResult moderationResult = openAiModerationService.checkContent(dto.getDescription());
        log.info("Moderation result: {}", moderationResult);

        handleModerationResult(existingAd, moderationResult);

        updateCarAdFields(existingAd, dto);

        final Map<CurrencyCode, BigDecimal> convertedPrices = currencyRateService.convertToAll(
                dto.getOriginalCurrency(),
                dto.getPrice()
        );
        helper.setConvertedPrices(existingAd, convertedPrices);

        final CarAd saved = carAdRepository.save(existingAd);
        log.info("User {} updated ad {} with status {}", existingAd.getSeller().getId(), saved.getId(), saved.getStatus());

        return carAdMapper.toDto(saved, isCurrentUserPremium());
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

        final UUID currentUserId = SecurityUtil.getCurrentUserId();

        final Specification<CarAd> filterSpec = CarAdSpecification.filterBy(
                status, brandId, modelId, yearFrom, yearTo, mileageFrom, mileageTo, priceFrom, priceTo, descriptionContains);

        final Specification<CarAd> userSpec = CarAdSpecification.ownedByUser(currentUserId);

        final Specification<CarAd> spec = filterSpec.and(userSpec);

        return carAdRepository.findAll(spec, pageable)
                .map(ad -> carAdMapper.toDto(ad, isCurrentUserPremium()));
    }

    @Override
    public CarAdResponseDto getMyAdById(UUID id) {
        final CarAd ad = helper.findAdOwnedByUser(id, SecurityUtil.getCurrentUserId());
        return carAdMapper.toDto(ad, isCurrentUserPremium());
    }

    @Override
    public void deleteAdById(UUID id) {
        final UUID currentUserId = SecurityUtil.getCurrentUserId();
        final CarAd ad = helper.findAdOwnedByUser(id, currentUserId);
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
        ad.setDescription(dto.getDescription());
    }

    private void handleModerationResult(CarAd ad, ModerationResult moderationResult) {
        int attempts = ad.getEditAttempts() == null ? 0 : ad.getEditAttempts();

        if (moderationResult == ModerationResult.FLAGGED) {
            attempts++;
            ad.setEditAttempts(attempts);

            if (attempts >= 3) {
                ad.setStatus(AdStatus.UNDER_MODERATION);
            } else {
                ad.setStatus(AdStatus.REJECTED);
            }

            carAdRepository.save(ad);
            log.warn("Description failed moderation. Attempts: {}. Current status: {}", attempts, ad.getStatus());
            throw new IllegalArgumentException("Опис містить заборонений контент. Редагування обмежено.");
        } else if (moderationResult == ModerationResult.QUOTA_EXCEEDED || moderationResult == ModerationResult.ERROR) {
            log.warn("OpenAI moderation issue: {}. Allowing update with status PENDING.", moderationResult);
            attempts++;
            ad.setEditAttempts(attempts);

            if (attempts >= 3) {
                ad.setStatus(AdStatus.UNDER_MODERATION);
            } else {
                ad.setStatus(AdStatus.PENDING);
            }
        } else {
            ad.setEditAttempts(0);
            ad.setStatus(AdStatus.ACTIVE);
        }
    }

    private boolean isCurrentUserPremium() {
        if (!SecurityUtil.isAuthenticated()) {
            return false;
        }
        return SecurityUtil.getCurrentUser().hasActiveSubscription();
    }
}