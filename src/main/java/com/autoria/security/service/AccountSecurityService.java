package com.autoria.security.service;

import com.autoria.models.user.dto.ChangeEmailRequestDto;
import com.autoria.models.user.dto.ChangePasswordRequestDto;

import java.util.UUID;

public interface AccountSecurityService {
    void changePassword(UUID id, ChangePasswordRequestDto changePasswordRequestDto);

    void changeEmail(UUID id, ChangeEmailRequestDto changeEmailRequestDto);
}
