package com.autoria.models.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class RoleRequestDto {
    @NotBlank
    private String name;

    private Set<UUID> permissionIds;

}
