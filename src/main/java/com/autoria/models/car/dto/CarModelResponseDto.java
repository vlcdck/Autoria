package com.autoria.models.car.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CarModelResponseDto {

    private UUID id;
    private String model;
    private UUID brandId;
    private String brandName;
}
