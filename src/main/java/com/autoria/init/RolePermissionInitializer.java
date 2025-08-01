package com.autoria.init;

import com.autoria.enums.PermissionCode;
import com.autoria.enums.RoleType;
import com.autoria.models.user.Permission;
import com.autoria.models.user.Role;
import com.autoria.repository.PermissionRepository;
import com.autoria.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolePermissionInitializer implements ApplicationRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        // Create all possible Permissions
        Arrays.stream(PermissionCode.values()).forEach(code ->
                permissionRepository.findByCode(code.name()).orElseGet(() ->
                        permissionRepository.save(Permission.builder()
                                .code(code.name())
                                .description(code.name().replace("_", " ").toLowerCase())
                                .build())));

        createRole(RoleType.BUYER, Set.of()); // without access to ads

        createRole(RoleType.SELLER, Set.of(
                PermissionCode.CREATE_OWN_AD,
                PermissionCode.VIEW_OWN_AD,
                PermissionCode.UPDATE_OWN_AD,
                PermissionCode.DELETE_OWN_AD
        ));

        createRole(RoleType.MANAGER, Set.of(
                PermissionCode.VIEW_ANY_AD,
                PermissionCode.UPDATE_ANY_AD,
                PermissionCode.DELETE_ANY_AD,
                PermissionCode.MANAGE_ADS,
                PermissionCode.APPROVE_AD,
                PermissionCode.BAN_USER
        ));

        createRole(RoleType.ADMIN, Set.of(PermissionCode.values())); // all
    }

    private void createRole(RoleType roleName, Set<PermissionCode> permissionCodes) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Set<Permission> permissions = permissionCodes.stream()
                    .map(code -> permissionRepository.findByCode(code.name()).orElseThrow())
                    .collect(Collectors.toSet());

            roleRepository.save(Role.builder()
                    .name(roleName)
                    .permissions(permissions)
                    .build());
        }
    }
}
