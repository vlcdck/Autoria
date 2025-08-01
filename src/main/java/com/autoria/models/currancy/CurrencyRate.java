package com.autoria.models.currancy;

import com.autoria.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "currency_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private CurrencyCode baseCurrency;

    @Enumerated(EnumType.STRING)
    private CurrencyCode targetCurrency;

    @Column(nullable = false)
    private BigDecimal rate;

    private LocalDate date;
}
