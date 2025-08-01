package com.autoria.models.ad.dto;

import com.autoria.enums.AdStatus;
import com.autoria.enums.CurrencyCode;
import com.autoria.enums.FuelType;
import com.autoria.validation.ValidYear;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class CarAdRequestDto {

    @NotNull
    private AdStatus status;

    @PositiveOrZero
    private int editAttempts;

    private UUID dealershipId; // nullable

    @NotNull(message = "Brand ID is required")
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

    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private CurrencyCode originalCurrency;

}
