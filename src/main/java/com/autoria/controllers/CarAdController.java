package com.autoria.controllers;

import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.services.ad.CarAdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/car-ads")
@RequiredArgsConstructor
public class CarAdController {

    private final CarAdService carAdService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_AD')")
    public ResponseEntity<CarAdResponseDto> createAd(@Valid @RequestBody CarAdRequestDto carAdRequestDto) {
        CarAdResponseDto carAdResponseDto = carAdService.createAd(carAdRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(carAdResponseDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_AD')")
    public ResponseEntity<CarAdResponseDto> getAdById(@PathVariable UUID id) {
        CarAdResponseDto carAdResponseDto = carAdService.getAdById(id);
        return ResponseEntity.ok(carAdResponseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_AD')")
    public ResponseEntity<CarAdResponseDto> updateAd(@PathVariable UUID id, @Valid @RequestBody CarAdRequestDto dto) {
        CarAdResponseDto updatedAd = carAdService.updateAd(id, dto);
        return ResponseEntity.ok(updatedAd);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('VIEW_OWN_ADS')")
    public ResponseEntity<List<CarAdResponseDto>> getMyAds() {
        List<CarAdResponseDto> myAds = carAdService.getMyAds();
        return ResponseEntity.ok(myAds);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_AD')")
    public ResponseEntity<Void> deleteAd(@PathVariable UUID id) {
        carAdService.deleteAdById(id);
        return ResponseEntity.noContent().build();
    }
}
