package com.autoria.controllers;

import com.autoria.models.car.CarBrand;
import com.autoria.models.car.CarModel;
import com.autoria.models.car.dto.MissingBrandRequestDto;
import com.autoria.services.catalog.CarCatalogService;
import com.autoria.services.catalog.MissingBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CarCatalogController {

    private final CarCatalogService carCatalogService;
    private final MissingBrandService missingBrandService;

    @GetMapping("/brands")
    public ResponseEntity<List<CarBrand>> getAllBrands() {
        return ResponseEntity.ok(carCatalogService.getAllBrands());
    }

    @GetMapping("/models")
    public ResponseEntity<List<CarModel>> getAllModels() {
        return ResponseEntity.ok(carCatalogService.getAllModels());
    }

    @GetMapping("/brands/{brandId}/models")
    public ResponseEntity<List<CarModel>> getModelsByBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(carCatalogService.getModelsByBrand(brandId));
    }

    @PostMapping("/missing")
    public ResponseEntity<String> reportMissingBrand(@Valid @RequestBody MissingBrandRequestDto dto) {
        missingBrandService.reportMissingBrand(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
