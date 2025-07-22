package com.autoria.mappers;

import com.autoria.models.user.Permission;
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
}
