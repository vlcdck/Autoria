package com.autoria.services.catalog;

import com.autoria.models.car.dto.MissingBrandRequestDto;

public interface MissingBrandService {
    void reportMissingBrand(MissingBrandRequestDto dto);

}
