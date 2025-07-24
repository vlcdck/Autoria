package com.autoria.mappers.role;

import com.autoria.enums.RoleType;
import com.autoria.models.user.Permission;
import com.autoria.models.user.Role;
import com.autoria.models.user.dto.RoleRequestDto;
import com.autoria.models.user.dto.RoleResponseDto;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleResponseDto toDto(Role role) {
        RoleResponseDto roleResponseDto = new RoleResponseDto();

        roleResponseDto.setId(role.getId());
        roleResponseDto.setName(role.getName().name());
        ;
        roleResponseDto.setPermissions(
                role.getPermissions().stream()
                        .map(permission -> permission.getCode())
                        .collect(Collectors.toSet())
        );
        return roleResponseDto;
    }

    public Role toEntity(RoleRequestDto dto, Set<Permission> permissions) {
        return Role.builder()
                .name(RoleType.valueOf(dto.getName()))
                .permissions(permissions)
                .build();
    }
}
