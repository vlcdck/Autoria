package com.autoria.models.user.dto;

import com.autoria.enums.AccountType;
import lombok.Data;

@Data
public class UpgradeAccountRequest {
    private AccountType newAccountType;
    private int durationInDays;
}