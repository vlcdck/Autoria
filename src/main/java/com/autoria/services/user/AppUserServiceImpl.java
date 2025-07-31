package com.autoria.services.user;

import com.autoria.enums.RoleType;
import com.autoria.mappers.user.AppUserMapper;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import com.autoria.models.user.Role;
import com.autoria.models.user.dto.AppUserRequestDto;
import com.autoria.models.user.dto.AppUserResponseDto;
import com.autoria.repository.AppUserRepository;
import com.autoria.repository.DealershipRepository;
import com.autoria.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final RoleRepository roleRepository;
    private final DealershipRepository dealershipRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AppUserResponseDto createAppUser(AppUserRequestDto appUserRequestDto) {


        if (appUserRepository.existsByEmail(appUserRequestDto.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (appUserRequestDto.getRoleIds() != null && !appUserRequestDto.getRoleIds().isEmpty()) {
            boolean hasAdminOrManagerRole = roleRepository.findAllById(appUserRequestDto.getRoleIds())
                    .stream()
                    .anyMatch(role -> role.getName() == RoleType.ADMIN || role.getName() == RoleType.MANAGER);

            if (hasAdminOrManagerRole) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                boolean isAdmin = auth != null && auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

                if (!isAdmin) {
                    throw new AccessDeniedException("Only admins can create users with ADMIN or MANAGER roles");
                }
            }
        }

        AppUser appUser = appUserMapper.toEntity(appUserRequestDto);
        appUser.setPassword(passwordEncoder.encode(appUserRequestDto.getPassword()));

        if (appUserRequestDto.getDealershipId() != null) {
            Dealership dealership = dealershipRepository.findById(appUserRequestDto.getDealershipId()).orElseThrow(() -> new EntityNotFoundException("Dealership not found"));
            appUser.setDealership(dealership);
        }

        Set<Role> roles = fetchRoles(appUserRequestDto.getRoleIds());
        appUser.setRoles(roles);
        return appUserMapper.toDto(appUserRepository.save(appUser));
    }

    @Override
    public List<AppUserResponseDto> getAllAppUsers() {
        return appUserRepository.findAll().stream().map(appUserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AppUserResponseDto getAppUserById(UUID id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return appUserMapper.toDto(appUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public AppUserResponseDto updateAppUser(UUID id, AppUserRequestDto appUserRequestDto) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        appUser.setFirstName(appUserRequestDto.getFirstName());
        appUser.setLastName(appUserRequestDto.getLastName());

        if (appUserRequestDto.getRoleIds() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                throw new AccessDeniedException("Only admins can update roles");
            }

            Set<Role> roles = fetchRoles(appUserRequestDto.getRoleIds());
            appUser.setRoles(roles);
        }

        if (appUserRequestDto.getDealershipId() != null) {
            Dealership dealership = dealershipRepository.findById(appUserRequestDto.getDealershipId())
                    .orElseThrow(() -> new EntityNotFoundException("Dealership not found"));
            appUser.setDealership(dealership);
        } else {
            appUser.setDealership(null);
        }
        return appUserMapper.toDto(appUserRepository.save(appUser));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAppUser(UUID id) {
        if (!appUserRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        appUserRepository.deleteById(id);
    }

    private Set<Role> fetchRoles(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }
}
