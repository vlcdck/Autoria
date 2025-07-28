package com.autoria.models.ad.dto;

import com.autoria.enums.AdStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CarAdFilterRequest {

    private AdStatus status;
    private UUID brandId;
    private UUID modelId;

    private Integer yearFrom;
    private Integer yearTo;

    private Integer mileageFrom;
    private Integer mileageTo;

    private BigDecimal priceFrom;
    private BigDecimal priceTo;
}
