package com.autoria.repository;

import com.autoria.enums.CurrencyCode;
import com.autoria.models.currancy.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface CurrancyRateRepository extends JpaRepository<CurrencyRate, UUID> {
    Optional<CurrencyRate> findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(
            CurrencyCode base,
            CurrencyCode target
    );
    void deleteByBaseCurrencyAndTargetCurrencyAndDate(
            CurrencyCode base,
            CurrencyCode target,
            LocalDate date
    );
}
