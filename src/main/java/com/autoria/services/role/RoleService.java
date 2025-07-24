package com.autoria.services.role;


import com.autoria.models.user.dto.RoleRequestDto;
import com.autoria.models.user.dto.RoleResponseDto;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleResponseDto createRole(RoleRequestDto roleRequestDto);

    List<RoleResponseDto> getAllRoles();

    RoleResponseDto getRoleById(UUID roleId);

    RoleResponseDto updateRole(UUID id, RoleRequestDto roleRequestDto);

    void deleteRole(UUID roleId);
}
