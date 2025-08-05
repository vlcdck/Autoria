package com.autoria.models.car.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MissingBrandRequestDto {
    @NotBlank
    private String brandName;

    private String modelName;

    @NotBlank
    private String message;

    @Email
    @NotBlank
    private String contactEmail;
}
