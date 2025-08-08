package com.autoria.init;

import com.autoria.enums.AccountType;
import com.autoria.enums.RoleType;
import com.autoria.models.user.AppUser;
import com.autoria.models.user.Role;
import com.autoria.repository.AppUserRepository;
import com.autoria.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(2)
public class AdminInitializer implements ApplicationRunner {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {



        // Check whether there is at least one admin
        if (userRepository.existsByRoles_Name(RoleType.ADMIN)) {
            System.out.println("Admin already exists!");
            return;
        }

        // Check if there's a user with this email
        if (userRepository.existsByEmail(adminEmail)) {
            System.out.println("User with email " + adminEmail + " already exists, but without role ADMIN.");
            return;
        }

        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "Role ADMIN not found. Make sure that RolePermissionInitializer runs first."
                ));

        // Create Admin
        AppUser admin = AppUser.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email(adminEmail)
                .phoneNumber("+380000000000")
                .password(passwordEncoder.encode(adminPassword))
                .roles(Set.of(adminRole))
                .enabled(true)
                .accountType(AccountType.PREMIUM)
                .build();

        userRepository.save(admin);
    }
}
