package com.autoria.services.user;

import com.autoria.models.user.dto.AppUserRequestDto;
import com.autoria.models.user.dto.AppUserResponseDto;

import java.util.List;
import java.util.UUID;

public interface AppUserService {
    AppUserResponseDto createAppUser(AppUserRequestDto appUserRequestDto);

    List<AppUserResponseDto> getAllAppUsers();

    AppUserResponseDto getAppUserById(UUID id);

    AppUserResponseDto updateAppUser(UUID id, AppUserRequestDto appUserRequestDto);

    void deleteAppUser(UUID id);
}
