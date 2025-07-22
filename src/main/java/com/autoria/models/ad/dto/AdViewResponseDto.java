package com.autoria.models.ad.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdViewResponseDto {
    private UUID id;
    private UUID carAdId;
    private LocalDateTime viewedAt;
    private String region;
}
