package com.autoria.controllers;

import com.autoria.models.ad.dto.CarAdRequestDto;
import com.autoria.models.ad.dto.CarAdResponseDto;
import com.autoria.services.ad.admin.CarAdAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class AdminCarAdController {

    private final CarAdAdminService carAdAdminService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_ANY_AD')")
    public ResponseEntity<CarAdResponseDto> getAnyAdById(@PathVariable UUID id) {
        return ResponseEntity.ok(carAdAdminService.getAnyAdById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_ANY_AD')")
    public ResponseEntity<CarAdResponseDto> updateAnyAd(@PathVariable UUID id, @Valid @RequestBody CarAdRequestDto dto) {
        return ResponseEntity.ok(carAdAdminService.updateAnyAd(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_ANY_AD')")
    public ResponseEntity<Void> deleteAnyAd(@PathVariable UUID id) {
        carAdAdminService.deleteAnyAd(id);
        return ResponseEntity.noContent().build();
    }
}
