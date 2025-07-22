package com.autoria.models.user.dto;

import com.autoria.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class AppUserRequestDto {

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    private String password;

    private UUID dealershipId; // could be null

    @NotNull
    private AccountType accountType;

    private Set<UUID> roleIds;

}
