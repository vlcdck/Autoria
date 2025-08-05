package com.autoria.models.user.dto;

import com.autoria.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class AppUserUpdateDto {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phoneNumber;

    private Set<UUID> roleIds;

    private UUID dealershipId;

    private AccountType accountType;

    private LocalDateTime subscriptionEndDate;

    private Boolean enabled;

}
