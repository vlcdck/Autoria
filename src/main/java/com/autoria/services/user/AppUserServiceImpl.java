package com.autoria.services.user;

import com.autoria.enums.AccountType;
import com.autoria.enums.RoleType;
import com.autoria.mappers.user.AppUserMapper;
import com.autoria.models.dealership.Dealership;
import com.autoria.models.user.AppUser;
import com.autoria.models.user.Role;
import com.autoria.models.user.dto.AppUserCreateDto;
import com.autoria.models.user.dto.AppUserResponseDto;
import com.autoria.models.user.dto.AppUserUpdateDto;
import com.autoria.models.user.dto.UpgradeAccountRequest;
import com.autoria.repository.AppUserRepository;
import com.autoria.repository.DealershipRepository;
import com.autoria.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    public AppUserResponseDto createAppUser(AppUserCreateDto dto) {
        if (appUserRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Перевірка ролей ADMIN/MANAGER
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            boolean hasAdminOrManagerRole = roleRepository.findAllById(dto.getRoleIds())
                    .stream()
                    .anyMatch(role -> role.getName() == RoleType.ADMIN || role.getName() == RoleType.MANAGER);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (hasAdminOrManagerRole && !isAdmin) {
                throw new AccessDeniedException("Only admins can create users with ADMIN or MANAGER roles");
            }
        }

        AppUser appUser = appUserMapper.toEntity(dto);
        appUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getDealershipId() != null) {
            Dealership dealership = dealershipRepository.findById(dto.getDealershipId())
                    .orElseThrow(() -> new EntityNotFoundException("Dealership not found"));
            appUser.setDealership(dealership);
        }

        Set<Role> roles = fetchRoles(dto.getRoleIds());
        appUser.setRoles(roles);

        // Встановлення enabled за логікою, наприклад, активуємо одразу
        appUser.setEnabled(true);

        return appUserMapper.toDto(appUserRepository.save(appUser));
    }

    @Override
    public Page<AppUserResponseDto> getAllAppUsers(Pageable pageable) {
        return appUserRepository.findAll(pageable)
                .map(appUserMapper::toDto);
    }

    @Override
    public AppUserResponseDto getAppUserById(UUID id) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return appUserMapper.toDto(appUser);
    }

    @Override
    @Transactional
    public AppUserResponseDto updateAppUser(UUID id, AppUserUpdateDto dto) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        appUser.setFirstName(dto.getFirstName());
        appUser.setLastName(dto.getLastName());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (dto.getRoleIds() != null) {
            if (!isAdmin) {
                throw new AccessDeniedException("Only admins can update roles");
            }
            Set<Role> roles = fetchRoles(dto.getRoleIds());
            appUser.setRoles(roles);
        }

        if (dto.getDealershipId() != null) {
            Dealership dealership = dealershipRepository.findById(dto.getDealershipId())
                    .orElseThrow(() -> new EntityNotFoundException("Dealership not found"));
            appUser.setDealership(dealership);
        } else {
            appUser.setDealership(null);
        }

        if (isAdmin) {
            if (dto.getAccountType() != null) {
                appUser.setAccountType(dto.getAccountType());
                if (appUser.getAccountType() == AccountType.BASIC) {
                    appUser.setSubscriptionEndDate(null);
                }
            }
            if (dto.getSubscriptionEndDate() != null) {
                appUser.setSubscriptionEndDate(dto.getSubscriptionEndDate());
            }
        }

        return appUserMapper.toDto(appUserRepository.save(appUser));
    }

    @Override
    @Transactional
    public void deleteAppUser(UUID id) {
        if (!appUserRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        appUserRepository.deleteById(id);
    }

    @Transactional
    public void upgradeAccount(UUID userId, UpgradeAccountRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.getNewAccountType() == AccountType.BASIC) {
            throw new IllegalArgumentException("Cannot downgrade to BASIC");
        }

        user.setAccountType(request.getNewAccountType());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentEnd = user.getSubscriptionEndDate();

        if (currentEnd != null && currentEnd.isAfter(now)) {
            user.setSubscriptionEndDate(currentEnd.plusDays(request.getDurationInDays()));
        } else {
            user.setSubscriptionEndDate(now.plusDays(request.getDurationInDays()));
        }

        appUserRepository.save(user);
    }

    private Set<Role> fetchRoles(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }
}
