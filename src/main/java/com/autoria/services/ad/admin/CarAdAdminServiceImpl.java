package com.autoria.services.ad.admin;

import com.autoria.mappers.car.CarAdMapper;
import com.autoria.models.ad.CarAd;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.repository.CarAdRepository;
import com.autoria.services.ad.util.CarAdHelperService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CarAdAdminServiceImpl implements CarAdAdminService {

    private final CarAdRepository carAdRepository;
    private final CarAdMapper carAdMapper;
    private final CarAdHelperService helper;

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
        ad.setPriceUSD(dto.getPriceUSD());
        ad.setPriceEUR(dto.getPriceEUR());
        ad.setPriceUAH(dto.getPriceUAH());
        ad.setExchangeRateSource(dto.getExchangeRateSource());
        ad.setExchangeRateDate(dto.getExchangeRateDate());
        ad.setDescription(dto.getDescription());

        // Адмін може змінювати статус напряму
        ad.setStatus(dto.getStatus());

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
