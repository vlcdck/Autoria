package com.autoria.security.jwt;

import com.autoria.models.RefreshRequest;
import com.autoria.models.authentication.AuthenticationRequest;
import com.autoria.models.authentication.AuthenticationResponse;
import com.autoria.models.register.RegisterRequest;
import com.autoria.models.user.AppUser;
import com.autoria.models.user.Role;
import com.autoria.repository.AppUserRepository;
import com.autoria.security.user.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        Set<Role> roles = new HashSet<>();
        // Якщо registerRequest.getRole() повертає String роль, потрібно конвертувати в Role:
        // Тут припустимо, що це Role
        roles.add(registerRequest.getRole());

        AppUser appUser = AppUser.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(roles)
                .build();

        AppUserDetails userDetails = new AppUserDetails(appUser);

        String jwtToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        appUser.setRefreshToken(refreshToken);

        appUserRepository.save(appUser);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword())
        );

        AppUser appUser = appUserRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authenticationRequest.getEmail()));

        AppUserDetails userDetails = new AppUserDetails(appUser);

        String jwtToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        appUser.setRefreshToken(refreshToken);
        appUserRepository.save(appUser);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refresh(RefreshRequest refreshRequest) {
        String token = refreshRequest.getRefreshToken();
        String username = jwtTokenProvider.extractUsername(token);

        AppUser appUser = appUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!token.equals(appUser.getRefreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        AppUserDetails userDetails = new AppUserDetails(appUser);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        appUser.setRefreshToken(newRefreshToken);
        appUserRepository.save(appUser);

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
