package com.autoria.security.service;

import com.autoria.models.user.AppUser;
import com.autoria.models.user.dto.ChangeAccountTypeDto;
import com.autoria.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AppUserRepository appUserRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void changeAccountType(ChangeAccountTypeDto changeAccountTypeDto) {
        AppUser user = appUserRepository.findById(changeAccountTypeDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setAccountType(changeAccountTypeDto.getAccountType());
        appUserRepository.save(user);
    }
}
