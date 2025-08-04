package com.autoria.security.service;

import com.autoria.models.user.AppUser;
import com.autoria.models.user.dto.ChangeEmailRequestDto;
import com.autoria.models.user.dto.ChangePasswordRequestDto;
import com.autoria.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountSecurityServiceImpl implements AccountSecurityService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(UUID id, ChangePasswordRequestDto changePasswordRequestDto) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), appUser.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        appUser.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        appUserRepository.save(appUser);
    }

    @Override
    @Transactional
    public void changeEmail(UUID id, ChangeEmailRequestDto changeEmailRequestDto) {
        AppUser user = appUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(changeEmailRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (appUserRepository.existsByEmail(changeEmailRequestDto.getNewEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setEmail(changeEmailRequestDto.getNewEmail());
        appUserRepository.save(user);
    }
}
