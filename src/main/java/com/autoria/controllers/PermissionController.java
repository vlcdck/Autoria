package com.autoria.controllers;

import com.autoria.models.user.dto.PermissionRequestDto;
import com.autoria.models.user.dto.PermissionResponseDto;
import com.autoria.services.permission.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionResponseDto> createPermission(@Valid @RequestBody PermissionRequestDto request) {
        PermissionResponseDto created = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PermissionResponseDto>> getAllPermissions() {
        List<PermissionResponseDto> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionResponseDto> getPermissionById(@PathVariable UUID id) {
        PermissionResponseDto permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionResponseDto> updatePermission(@PathVariable UUID id,
                                                                  @Valid @RequestBody PermissionRequestDto request) {
        PermissionResponseDto updated = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
