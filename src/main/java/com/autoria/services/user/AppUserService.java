package com.autoria.services.user;

import com.autoria.models.user.dto.AppUserCreateDto;
import com.autoria.models.user.dto.AppUserResponseDto;
import com.autoria.models.user.dto.AppUserUpdateDto;
import com.autoria.models.user.dto.UpgradeAccountRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AppUserService {
    AppUserResponseDto createAppUser(AppUserCreateDto appUserCreateDto);


    Page<AppUserResponseDto> getAllAppUsers(Pageable pageable);

    AppUserResponseDto getAppUserById(UUID id);

    AppUserResponseDto updateAppUser(UUID id, AppUserUpdateDto appUserUpdateDto);

    void upgradeAccount(UUID userId, UpgradeAccountRequest request);

    void deleteAppUser(UUID id);

    void banUser(UUID userId);

    void unbanUser(UUID userId);
}
