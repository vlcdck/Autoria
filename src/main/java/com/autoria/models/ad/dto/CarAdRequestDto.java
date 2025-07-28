package com.autoria.models.ad.dto;

import com.autoria.enums.AdStatus;
import com.autoria.enums.CurrencyCode;
import com.autoria.enums.FuelType;
import com.autoria.validation.ValidYear;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CarAdRequestDto {

    @NotNull
    private AdStatus status;

    @PositiveOrZero
    private int editAttempts;

    private UUID dealershipId; // nullable

    @NotNull
    private UUID brandId;

    @NotNull
    private UUID modelId;

    @ValidYear
    private int year;

    @PositiveOrZero
    private Integer mileage;

    private FuelType fuelType;

    @PositiveOrZero
    private Integer ownersCount;

    private List<String> photos;

    @NotNull
    private CurrencyCode originalCurrency;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Positive
    private BigDecimal priceUSD;

    @NotNull
    @Positive
    private BigDecimal priceEUR;

    @NotNull
    @Positive
    private BigDecimal priceUAH;

    private String exchangeRateSource;

    @NotNull
    private LocalDateTime exchangeRateDate;
}
