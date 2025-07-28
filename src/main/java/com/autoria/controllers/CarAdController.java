package com.autoria.controllers;

import com.autoria.models.ad.dto.CarAdFilterRequest;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.services.ad.CarAdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class CarAdController {

    private final CarAdService carAdService;

    @GetMapping
    public ResponseEntity<Page<CarAdResponseDto>> getAllAds(Pageable pageable) {
        Page<CarAdResponseDto> ads = carAdService.findAll(null, pageable);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<CarAdResponseDto>> filterAds(
            @ModelAttribute CarAdFilterRequest filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(carAdService.filterAds(
                filter.getStatus(),
                filter.getBrandId(),
                filter.getModelId(),
                filter.getYearFrom(),
                filter.getYearTo(),
                filter.getMileageFrom(),
                filter.getMileageTo(),
                filter.getPriceFrom(),
                filter.getPriceTo(),
                pageable
        ));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_OWN_AD')")
    public ResponseEntity<CarAdResponseDto> createAd(@Valid @RequestBody CarAdRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carAdService.createAd(dto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('VIEW_OWN_AD')")
    public ResponseEntity<Page<CarAdResponseDto>> getMyAds(Pageable pageable) {
        return ResponseEntity.ok(carAdService.getMyAds(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_OWN_AD')")
    public ResponseEntity<CarAdResponseDto> getOwnAd(@PathVariable UUID id) {
        return ResponseEntity.ok(carAdService.getAdById(id)); // перевірка власності всередині
    }

    @GetMapping("/{id}/admin")
    @PreAuthorize("hasAuthority('VIEW_ANY_AD')")
    public ResponseEntity<CarAdResponseDto> getAnyAd(@PathVariable UUID id) {
        return ResponseEntity.ok(carAdService.getAnyAdById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_OWN_AD')")
    public ResponseEntity<CarAdResponseDto> updateOwnAd(@PathVariable UUID id, @Valid @RequestBody CarAdRequestDto dto) {
        return ResponseEntity.ok(carAdService.updateAd(id, dto));
    }

    @PutMapping("/{id}/admin")
    @PreAuthorize("hasAuthority('UPDATE_ANY_AD')")
    public ResponseEntity<CarAdResponseDto> updateAnyAd(@PathVariable UUID id, @Valid @RequestBody CarAdRequestDto dto) {
        return ResponseEntity.ok(carAdService.updateAnyAd(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_OWN_AD')")
    public ResponseEntity<Void> deleteOwnAd(@PathVariable UUID id) {
        carAdService.deleteAdById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/admin")
    @PreAuthorize("hasAuthority('DELETE_ANY_AD')")
    public ResponseEntity<Void> deleteAnyAd(@PathVariable UUID id) {
        carAdService.deleteAnyAd(id);
        return ResponseEntity.noContent().build();
    }
}
