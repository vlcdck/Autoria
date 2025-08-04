package com.autoria.models.ad.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CarAdAnalyticsDto {
    private long totalViews;
    private long dailyViews;
    private long weeklyViews;
    private long monthlyViews;
    private BigDecimal avgPriceRegion;
    private BigDecimal avgPriceUkraine;
}
