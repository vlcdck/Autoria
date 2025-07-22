package com.autoria.models.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CarModelRequestDto {
    @NotBlank
    @Size(max = 100)
    private String model;

    @NotNull
    private UUID brandId; // link to CarBrand
}
