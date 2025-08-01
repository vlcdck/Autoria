package com.autoria.services.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyScheduler {

    private final CurrencyRateService currencyRateService;

    @Scheduled(cron = "0 0 3 * * *") // every day at 3AM
    public void scheduledCurrencyUpdate() {
        currencyRateService.updateRatesFromPrivatBank();
    }
}