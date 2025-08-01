package com.autoria.init;

import com.autoria.services.currency.CurrencyRateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyInitializer {

    private final CurrencyRateService currencyRateService;

    @PostConstruct
    public void init() {
        currencyRateService.updateRatesFromPrivatBank();
    }
}