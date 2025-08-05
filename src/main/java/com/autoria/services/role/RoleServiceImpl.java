package com.autoria.services.role;


import com.autoria.enums.RoleType;
import com.autoria.mappers.role.RoleMapper;
import com.autoria.models.user.Permission;
import com.autoria.models.user.Role;
import com.autoria.models.user.dto.RoleRequestDto;
import com.autoria.models.user.dto.RoleResponseDto;
import com.autoria.repository.PermissionRepository;
import com.autoria.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleResponseDto createRole(RoleRequestDto roleRequestDto) {
        if (roleRepository.existsByName(roleRequestDto.getName())) {
            throw new IllegalArgumentException("Role with this name already exists");
        }

        Set<Permission> permissions = fetchPermissions(roleRequestDto.getPermissionIds());
        Role role = Role.builder()
                .name(RoleType.valueOf(roleRequestDto.getName().toUpperCase()))
                .permissions(permissions)
                .build();
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public RoleResponseDto getRoleById(UUID roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional
    public RoleResponseDto updateRole(UUID id, RoleRequestDto roleRequestDto) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));

        String newName = roleRequestDto.getName().toUpperCase();
        if (!role.getName().name().equals(newName) && roleRepository.existsByName(newName)) {
            throw new IllegalArgumentException("Role with this name already exists");
        }
        role.setName(RoleType.valueOf(newName));

        Set<Permission> permissions = fetchPermissions(roleRequestDto.getPermissionIds());
        role.setPermissions(permissions);

        return roleMapper.toDto(roleRepository.save(role));
    }


    @Override
    @Transactional
    public void deleteRole(UUID roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new EntityNotFoundException("Role not found");
        }
        roleRepository.deleteById(roleId);
    }

    private Set<Permission> fetchPermissions(Set<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        return new HashSet<>(permissions);
    }
}
