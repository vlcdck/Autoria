package com.autoria.mappers;

import com.autoria.models.user.Role;
import com.autoria.models.user.dto.RoleResponseDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleResponseDto toDto(Role role) {
        RoleResponseDto roleResponseDto = new RoleResponseDto();

        roleResponseDto.setId(role.getId());
        roleResponseDto.setName(role.getName());
        roleResponseDto.setPermissions(
                role.getPermissions().stream()
                        .map(permission -> permission.getCode())
                        .collect(Collectors.toSet())
        );
        return roleResponseDto;
    }

}
