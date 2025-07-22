package com.autoria.models.ad.dto;

import com.autoria.enums.AdStatus;
import com.autoria.enums.CurrencyCode;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CarAdRequestDto {

    @NotNull
    private UUID sellerId;

    @NotNull
    private AdStatus status;

    @PositiveOrZero
    private int editAttempts;

    @NotNull
    private UUID dealershipId;
    @NotNull
    private UUID brandId;
    @NotNull
    private UUID modelId;

    @Min(1886)
    private int year;

    @NotNull
    private CurrencyCode originalCurrency;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Positive
    private BigDecimal priseUSD;

    @NotNull
    @Positive
    private BigDecimal priseEUR;

    @NotNull
    @Positive
    private BigDecimal priseUAH;

    @NotBlank
    private String exchangeRateSource;

    @NotBlank
    private LocalDateTime exchangeRateDate;
}
