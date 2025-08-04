package com.autoria.controllers;

import com.autoria.models.user.dto.AppUserCreateDto;
import com.autoria.models.user.dto.AppUserResponseDto;
import com.autoria.models.user.dto.AppUserUpdateDto;
import com.autoria.models.user.dto.UpgradeAccountRequest;
import com.autoria.services.user.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    // Створення користувача - лише ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppUserResponseDto> createUser(@Valid @RequestBody AppUserCreateDto request) {
        AppUserResponseDto createdUser = appUserService.createAppUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Отримання списку усіх користувачів - лише ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AppUserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AppUserResponseDto> usersPage = appUserService.getAllAppUsers(pageable);
        return ResponseEntity.ok(usersPage);
    }

    // Отримання одного користувача - ADMIN або власник акаунту
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<AppUserResponseDto> getUserById(@PathVariable UUID id) {
        AppUserResponseDto user = appUserService.getAppUserById(id);
        return ResponseEntity.ok(user);
    }

    // Оновлення користувача - ADMIN або власник акаунту
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<AppUserResponseDto> updateUser(@PathVariable UUID id,
                                                         @Valid @RequestBody AppUserUpdateDto request) {
        AppUserResponseDto updatedUser = appUserService.updateAppUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    // Видалення користувача - лише ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        appUserService.deleteAppUser(id);
        return ResponseEntity.noContent().build();
    }

    // Апгрейд акаунту - лише ADMIN
    @PostMapping("/upgrade/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> upgradeAccount(@PathVariable("id") UUID userId,
                                               @Valid @RequestBody UpgradeAccountRequest request) {
        appUserService.upgradeAccount(userId, request);
        return ResponseEntity.ok().build();
    }
}

