package com.autoria.services.ad.admin;

import com.autoria.enums.CurrencyCode;
import com.autoria.mappers.car.CarAdMapper;
import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.repository.CarAdRepository;
import com.autoria.services.ad.util.CarAdHelperService;
import com.autoria.services.currency.CurrencyRateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CarAdAdminServiceImpl implements CarAdAdminService {

    private final CarAdRepository carAdRepository;
    private final CarAdMapper carAdMapper;
    private final CarAdHelperService helper;
    private final CurrencyRateService currencyRateService;

    @Override
    public CarAdResponseDto getAnyAdById(UUID id) {
        CarAd ad = carAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car ad with id " + id + " not found"));
        return carAdMapper.toDto(ad);
    }

    @Override
    public CarAdResponseDto updateAnyAd(UUID id, CarAdRequestDto dto) {
        CarAd ad = carAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car ad with id " + id + " not found"));

        helper.validateStatusTransition(ad.getStatus(), dto.getStatus());

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

        ad.setStatus(dto.getStatus());

        Map<CurrencyCode, BigDecimal> convertedPrices = currencyRateService.convertToAll(
                dto.getOriginalCurrency(),
                dto.getPrice()
        );

        helper.setConvertedPrices(ad, convertedPrices);

        CarAd saved = carAdRepository.save(ad);
        log.info("Admin updated ad {} with status {}", saved.getId(), saved.getStatus());

        return carAdMapper.toDto(saved);
    }

    @Override
    public void deleteAnyAd(UUID id) {
        if (!carAdRepository.existsById(id)) {
            throw new EntityNotFoundException("Car ad with id " + id + " not found");
        }
        carAdRepository.deleteById(id);
        log.info("Admin deleted ad {}", id);
    }
}
