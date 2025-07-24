package com.autoria.services.permission;

import com.autoria.models.user.dto.PermissionRequestDto;
import com.autoria.models.user.dto.PermissionResponseDto;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    PermissionResponseDto createPermission(PermissionRequestDto permissionRequestDto);

    List<PermissionResponseDto> getAllPermissions();

    PermissionResponseDto getPermissionById(UUID permissionId);

    PermissionResponseDto updatePermission(UUID permissionId, PermissionRequestDto permissionRequestDto);

    void deletePermission(UUID permissionId);
}
