package com.autoria.models.ad.dto;


import com.autoria.enums.AdStatus;
import com.autoria.enums.CurrencyCode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CarAdResponseDto {
    private UUID id;
    private UUID sellerId;
    private AdStatus status;
    private int editAttempts;
    private UUID dealershipId;
    private UUID brandId;
    private UUID modelId;
    private int year;
    private CurrencyCode originalCurrency;
    private BigDecimal price;
    private BigDecimal priceUSD;
    private BigDecimal priceUAH;
    private BigDecimal priceEUR;
    private String exchangeRateSource;
    private LocalDateTime exchangeRateDate;
}
