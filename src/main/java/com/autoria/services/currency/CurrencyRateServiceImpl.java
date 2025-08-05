package com.autoria.services.currency;

import com.autoria.enums.CurrencyCode;
import com.autoria.models.currancy.CurrencyRate;
import com.autoria.models.currancy.dto.PrivatBankRateResponse;
import com.autoria.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateServiceImpl implements CurrencyRateService {

    @Value("${privatbank.api.url}")
    private String PRIVAT_API_URL;

    private final CurrencyRateRepository currencyRateRepository;
    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    @Transactional
    public void updateRatesFromPrivatBank() {
        try {
            ResponseEntity<PrivatBankRateResponse[]> response =
                    restTemplate.getForEntity(PRIVAT_API_URL, PrivatBankRateResponse[].class);

            List<PrivatBankRateResponse> rates = Arrays.asList(Objects.requireNonNull(response.getBody()));

            for (PrivatBankRateResponse r : rates) {
                CurrencyCode base = CurrencyCode.valueOf(r.getCcy());
                CurrencyCode target = CurrencyCode.valueOf(r.getBase_ccy());
                BigDecimal rate = new BigDecimal(r.getSale());

                // Removing old courses of this type for today.
                currencyRateRepository.deleteByBaseCurrencyAndTargetCurrencyAndDate(base, target, LocalDate.now());

                // Keep only the direct course
                CurrencyRate directRate = new CurrencyRate();
                directRate.setBaseCurrency(base);
                directRate.setTargetCurrency(target);
                directRate.setRate(rate);
                directRate.setDate(LocalDate.now());

                currencyRateRepository.save(directRate);
            }

            log.info("Currency rates successfully updated from PrivatBank");

        } catch (Exception e) {
            log.error("Error updating currency rates: {}", e.getMessage());
        }
    }

    public BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount) {
        if (from.equals(to)) return amount;

        // First priority is a direct course
        Optional<CurrencyRate> direct = currencyRateRepository
                .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(from, to);

        if (direct.isPresent()) {
            return amount.multiply(direct.get().getRate());
        }

        // The second is the reverse course.
        Optional<CurrencyRate> inverse = currencyRateRepository
                .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(to, from);

        if (inverse.isPresent()) {
            BigDecimal invertedRate = BigDecimal.ONE.divide(inverse.get().getRate(), 10, RoundingMode.HALF_UP);
            return amount.multiply(invertedRate);
        }

        // The third one is through UAH (only if from ≠ UAH and to ≠ UAH)
        if (!from.equals(CurrencyCode.UAH) && !to.equals(CurrencyCode.UAH)) {
            Optional<CurrencyRate> fromToUAH = currencyRateRepository
                    .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(from, CurrencyCode.UAH);

            Optional<CurrencyRate> toToUAH = currencyRateRepository
                    .findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(to, CurrencyCode.UAH);

            if (fromToUAH.isPresent() && toToUAH.isPresent()) {
                BigDecimal uahAmount = amount.multiply(fromToUAH.get().getRate());
                BigDecimal eurRate = BigDecimal.ONE.divide(toToUAH.get().getRate(), 10, RoundingMode.HALF_UP);
                return uahAmount.multiply(eurRate);
            }
        }

        throw new RuntimeException("Currency not found: " + from + " → " + to);
    }

    // Method for converting to all currencies
    public Map<CurrencyCode, BigDecimal> convertToAll(CurrencyCode from, BigDecimal amount) {
        Map<CurrencyCode, BigDecimal> result = new EnumMap<>(CurrencyCode.class);

        for (CurrencyCode code : CurrencyCode.values()) {
            BigDecimal converted = convert(from, code, amount);
            result.put(code, converted);
        }

        return result;
    }
}
