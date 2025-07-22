package com.autoria.models.user.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class RoleResponseDto {
    private UUID id;
    private String name;
    private Set<String> permissions;
}
