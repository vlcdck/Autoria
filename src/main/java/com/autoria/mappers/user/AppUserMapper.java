package com.autoria.mappers.user;

import com.autoria.models.user.AppUser;
import com.autoria.models.user.Role;
import com.autoria.models.user.dto.AppUserCreateDto;
import com.autoria.models.user.dto.AppUserResponseDto;
import com.autoria.models.user.dto.AppUserUpdateDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AppUserMapper {

    public AppUser toEntity(AppUserCreateDto dto) {
        if (dto == null) {
            return null;
        }

        return AppUser.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .password(dto.getPassword())
                .accountType(dto.getAccountType())
                .subscriptionEndDate(dto.getSubscriptionEndDate())
                .build();
    }

    public void updateEntity(AppUser entity, AppUserUpdateDto dto) {
        if (entity == null || dto == null) {
            return;
        }
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());

        if (dto.getPhoneNumber() != null) {
            entity.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getAccountType() != null) {
            entity.setAccountType(dto.getAccountType());
        }

        if (dto.getSubscriptionEndDate() != null) {
            entity.setSubscriptionEndDate(dto.getSubscriptionEndDate());
        }

        if (dto.getEnabled() != null) {
            entity.setEnabled(dto.getEnabled());
        }
    }

    public AppUserResponseDto toDto(AppUser entity) {
        if (entity == null) {
            return null;
        }

        AppUserResponseDto dto = new AppUserResponseDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setAccountType(entity.getAccountType());
        dto.setSubscriptionEndDate(entity.getSubscriptionEndDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setEnabled(entity.isEnabled());
        dto.setDealershipId(entity.getDealership() != null ? entity.getDealership().getId() : null);

        Set<Role> roles = entity.getRoles();
        dto.setRoleIds(roles != null
                ? roles.stream().map(Role::getId).collect(Collectors.toSet())
                : Collections.emptySet());

        return dto;
    }
}
