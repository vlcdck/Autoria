package com.autoria.mappers.permission;

import com.autoria.models.user.Permission;
import com.autoria.models.user.dto.PermissionRequestDto;
import com.autoria.models.user.dto.PermissionResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionResponseDto toDto(Permission permission) {
        PermissionResponseDto permissionResponseDto = new PermissionResponseDto();
        permissionResponseDto.setId(permission.getId());
        permissionResponseDto.setCode(permission.getCode());
        permissionResponseDto.setDescription(permission.getDescription());
        return permissionResponseDto;
    }

    public Permission toEntity(PermissionRequestDto permissionRequestDto) {
        return Permission.builder()
                .code(permissionRequestDto.getCode())
                .description(permissionRequestDto.getDescription())
                .build();
    }
}
