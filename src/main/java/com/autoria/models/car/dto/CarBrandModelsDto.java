package com.autoria.models.car.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarBrandModelsDto {
    private String brand;
    private List<String> models;
}
