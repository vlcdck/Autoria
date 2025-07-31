package com.autoria.models.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SimpleEntityDto {
    private UUID id;
    private String name;
}
