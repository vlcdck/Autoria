package com.autoria.controllers;

import com.autoria.models.user.dto.ChangeEmailRequestDto;
import com.autoria.models.user.dto.ChangePasswordRequestDto;
import com.autoria.security.service.AccountSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountSecurityController {

    private final AccountSecurityService accountSecurityService;

    @PutMapping("/change-password/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        accountSecurityService.changePassword(id, changePasswordRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-email/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> changeEmail(
            @PathVariable UUID id,
            @RequestBody ChangeEmailRequestDto changeEmailRequestDto) {
        accountSecurityService.changeEmail(id, changeEmailRequestDto);
        return ResponseEntity.noContent().build();
    }
}
