package com.autoria.services.currency;

import com.autoria.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyRateService {
    void updateRatesFromPrivatBank();
    BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount);
    Map<CurrencyCode, BigDecimal> convertToAll(CurrencyCode from, BigDecimal amount);
}
