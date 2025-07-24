package com.autoria.mappers.user;

import com.autoria.models.user.AppUser;
import com.autoria.models.user.Role;
import com.autoria.models.user.dto.AppUserRequestDto;
import com.autoria.models.user.dto.AppUserResponseDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AppUserMapper {

    public AppUser toEntity(AppUserRequestDto appUserRequestDto) {
        return AppUser.builder()
                .firstName(appUserRequestDto.getFirstName())
                .lastName(appUserRequestDto.getLastName())
                .email(appUserRequestDto.getEmail())
                .password(appUserRequestDto.getPassword()) // HASH AT SERVICE REMAKE
                .accountType(appUserRequestDto.getAccountType())
                .build();
        // dealership та ролі потрібно встановити окремо в сервісі після пошуку по ID
    }

    public AppUserResponseDto toDto(AppUser appUser) {
        AppUserResponseDto appUserResponseDto = new AppUserResponseDto();
        appUserResponseDto.setId(appUser.getId());
        appUserResponseDto.setFirstName(appUser.getFirstName());
        appUserResponseDto.setLastName(appUser.getLastName());
        appUserResponseDto.setEmail(appUser.getEmail());
        appUserResponseDto.setAccountType(appUser.getAccountType());
        appUserResponseDto.setCreatedAt(appUser.getCreatedAt());
        appUserResponseDto.setUpdatedAt(appUser.getUpdatedAt());
        appUserResponseDto.setDealershipId(appUser.getDealership() != null ? appUser.getDealership().getId() : null);
        appUserResponseDto.setRoleIds(appUser.getRoles() != null ? appUser.getRoles()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet())
                : null);
        return appUserResponseDto;
    }
}
