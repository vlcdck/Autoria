package com.autoria.models.user.dto;

import com.autoria.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class AppUserCreateDto {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(min = 8)
    private String password;

    private Set<UUID> roleIds;

    private UUID dealershipId;

    private AccountType accountType;

    private LocalDateTime subscriptionEndDate;
}
