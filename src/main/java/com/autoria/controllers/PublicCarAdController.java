package com.autoria.controllers;

import com.autoria.models.ad.dto.CarAdFilterRequest;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.services.ad.user.CarAdUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class PublicCarAdController {

    private final CarAdUserService carAdUserService;

    @GetMapping
    public ResponseEntity<Page<CarAdResponseDto>> getAds(
            @ModelAttribute CarAdFilterRequest filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(carAdUserService.filterAds(
                filter.getStatus(),
                filter.getBrandId(),
                filter.getModelId(),
                filter.getYearFrom(),
                filter.getYearTo(),
                filter.getMileageFrom(),
                filter.getMileageTo(),
                filter.getPriceFrom(),
                filter.getPriceTo(),
                filter.getDescriptionContains(),
                pageable
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarAdResponseDto> getAdById(@PathVariable UUID id) {
        return ResponseEntity.ok(carAdUserService.getAdById(id));
    }


}
