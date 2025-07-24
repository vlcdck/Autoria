package com.autoria.models.user.dto;

import com.autoria.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeAccountTypeDto {
    @NotNull
    private UUID id;

    @NotNull
    private AccountType accountType;
}
