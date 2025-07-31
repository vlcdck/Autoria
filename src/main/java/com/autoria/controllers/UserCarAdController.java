package com.autoria.controllers;

import com.autoria.models.ad.dto.CarAdFilterRequest;
import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.services.ad.user.CarAdUserService;
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
@RequestMapping("/api/v1/listings/my")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('VIEW_OWN_AD') or hasRole('MANAGER')")
public class UserCarAdController {

    private final CarAdUserService carAdUserService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_OWN_AD')")
    public ResponseEntity<CarAdResponseDto> createAd(@Valid @RequestBody CarAdRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carAdUserService.createAd(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_OWN_AD')")
    public ResponseEntity<Page<CarAdResponseDto>> getMyAds(
            @ModelAttribute CarAdFilterRequest filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(carAdUserService.getMyAds(
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
    @PreAuthorize("hasAuthority('VIEW_OWN_AD')")
    public ResponseEntity<CarAdResponseDto> getMyAdById(@PathVariable UUID id) {
        return ResponseEntity.ok(carAdUserService.getMyAdById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_OWN_AD')")
    public ResponseEntity<CarAdResponseDto> updateOwnAd(@PathVariable UUID id, @Valid @RequestBody CarAdRequestDto dto) {
        return ResponseEntity.ok(carAdUserService.updateAd(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_OWN_AD')")
    public ResponseEntity<Void> deleteOwnAd(@PathVariable UUID id) {
        carAdUserService.deleteAdById(id);
        return ResponseEntity.noContent().build();
    }
}
