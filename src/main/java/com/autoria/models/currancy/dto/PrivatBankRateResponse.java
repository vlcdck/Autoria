package com.autoria.models.currancy.dto;

import lombok.Data;

@Data
public class PrivatBankRateResponse {
    private String ccy;
    private String base_ccy;
    private String buy;
    private String sale;
}
