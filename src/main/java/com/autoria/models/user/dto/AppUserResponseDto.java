package com.autoria.models.user.dto;

import com.autoria.enums.AccountType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class AppUserResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UUID dealershipId;
    private AccountType accountType;
    private Set<UUID> roleIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
