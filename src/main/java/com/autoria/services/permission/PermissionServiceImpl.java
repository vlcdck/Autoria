package com.autoria.services.permission;

import com.autoria.mappers.permission.PermissionMapper;
import com.autoria.models.user.Permission;
import com.autoria.models.user.dto.PermissionRequestDto;
import com.autoria.models.user.dto.PermissionResponseDto;
import com.autoria.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional
    public PermissionResponseDto createPermission(PermissionRequestDto permissionRequestDto) {
        if (permissionRepository.existsByCode(permissionRequestDto.getCode())) {
            throw new IllegalArgumentException("Permission with this code already exists");
        }

        Permission permission = permissionMapper.toEntity(permissionRequestDto);
        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    @Override
    public List<PermissionResponseDto> getAllPermissions() {
        return permissionRepository.findAll().stream().map(permissionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public PermissionResponseDto getPermissionById(UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + permissionId));
        return permissionMapper.toDto(permission);
    }

    @Override
    @Transactional
    public PermissionResponseDto updatePermission(UUID permissionId, PermissionRequestDto permissionRequestDto) {
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + permissionId));

        permission.setCode(permissionRequestDto.getCode());
        permission.setDescription(permissionRequestDto.getDescription());

        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public void deletePermission(UUID permissionId) {
        if (!permissionRepository.existsById(permissionId)) {
            throw new EntityNotFoundException("Permission not found with id: " + permissionId);
        }
        permissionRepository.deleteById(permissionId);

    }
}
