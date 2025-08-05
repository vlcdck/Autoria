package com.autoria.controllers;

import com.autoria.models.user.dto.RoleRequestDto;
import com.autoria.models.user.dto.RoleResponseDto;
import com.autoria.services.role.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto request) {
        RoleResponseDto createdRole = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable UUID id) {
        RoleResponseDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable UUID id,
                                                      @Valid @RequestBody RoleRequestDto request) {
        RoleResponseDto updatedRole = roleService.updateRole(id, request);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}