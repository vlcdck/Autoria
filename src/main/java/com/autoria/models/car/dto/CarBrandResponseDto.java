package com.autoria.models.car.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CarBrandResponseDto {
    private UUID id;
    private String name;
}
