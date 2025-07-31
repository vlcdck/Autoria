package com.autoria.models.ad.dto;


import com.autoria.enums.AdStatus;
import com.autoria.enums.CurrencyCode;
import com.autoria.enums.FuelType;
import com.autoria.models.user.dto.SimpleAppUserDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CarAdResponseDto {
    private UUID id;
    private SimpleAppUserDto seller;
    private AdStatus status;
    private int editAttempts;
    private SimpleEntityDto dealership;
    private SimpleEntityDto brand;
    private SimpleEntityDto model;
    private int year;
    private Integer mileage;
    private FuelType fuelType;
    private Integer ownersCount;
    private List<String> photos;
    private String description;
    private CurrencyCode originalCurrency;
    private BigDecimal price;
    private BigDecimal priceUSD;
    private BigDecimal priceUAH;
    private BigDecimal priceEUR;
    private String exchangeRateSource;
    private LocalDateTime exchangeRateDate;
}

