package com.autoria.mappers.car;

import com.autoria.models.car.CarBrand;
import com.autoria.models.car.dto.CarBrandRequestDto;
import com.autoria.models.car.dto.CarBrandResponseDto;
import lombok.Data;

@Data
public class CarBrandMapper {

    public CarBrand toEntity(CarBrandRequestDto carBrandRequestDto) {
        return CarBrand.builder()
                .name(carBrandRequestDto.getName())
                .build();
    }

    public CarBrandResponseDto toDto(CarBrand carBrand) {
        CarBrandResponseDto carBrandResponseDto = new CarBrandResponseDto();

        carBrandResponseDto.setId(carBrand.getId());
        carBrandResponseDto.setName(carBrand.getName());
        return carBrandResponseDto;
    }
}
