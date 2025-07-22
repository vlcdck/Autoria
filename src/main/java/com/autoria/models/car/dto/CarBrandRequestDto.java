package com.autoria.models.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CarBrandRequestDto {
    @NotBlank
    @Size(max = 100)
    private String name;
}
