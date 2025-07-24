package com.autoria.models.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionRequestDto {
    @NotBlank
    private String code;
    private String description;
}
