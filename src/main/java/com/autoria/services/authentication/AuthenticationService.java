package com.autoria.services.authentication;

import com.autoria.enums.AccountType;
import com.autoria.enums.RoleType;
import com.autoria.exeptions.EmailAlreadyConfirmedException;
import com.autoria.exeptions.EmailAlreadyTakenException;
import com.autoria.exeptions.InvalidRefreshTokenException;
import com.autoria.exeptions.TokenExpiredException;
import com.autoria.models.authentication.AuthenticationRequest;
import com.autoria.models.authentication.AuthenticationResponse;
import com.autoria.models.refresh.RefreshRequest;
import com.autoria.models.register.RegisterRequest;
import com.autoria.models.register.RegisterResponse;
import com.autoria.models.token.ConfirmationToken;
import com.autoria.models.user.AppUser;
import com.autoria.models.user.Role;
import com.autoria.repository.AppUserRepository;
import com.autoria.repository.ConfirmationTokenRepository;
import com.autoria.repository.RoleRepository;
import com.autoria.security.jwt.JwtTokenProvider;
import com.autoria.security.user.AppUserDetails;
import com.autoria.services.email.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSenderService emailSenderService;

    @Value("${jwt.email-token.expiration}")
    private int emailTokenExpiration;

    @Value("${app.auth.confirmation-url}")
    private String confirmationUrl;


    public RegisterResponse register(RegisterRequest registerRequest) {
        if (appUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyTakenException("Email already taken");
        }

        Role baseRole = roleRepository.findByName(RoleType.SELLER)
                .orElseThrow(() -> new RuntimeException("Base role USER not found"));

        AppUser appUser = AppUser.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Set.of(baseRole))
                .accountType(AccountType.BASIC)
                .enabled(false)
                .build();

        AppUser savedUser = appUserRepository.save(appUser);

        String token = createAndSaveConfirmationToken(savedUser);

        String link = confirmationUrl + token;
        emailSenderService.sendConfirmationToken(savedUser.getEmail(), buildEmail(savedUser.getFirstName(), link));

        RegisterResponse response = new RegisterResponse();
        response.setMessage("Registration successful! Please check your email to confirm your account.");
        return response;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        AppUser appUser = findUserByEmail(authenticationRequest.getEmail());

        if (!appUser.isEnabled()) {
            throw new EmailAlreadyConfirmedException("Email already confirmed");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );

        return generateTokensAndSave(appUser);
    }

    public AuthenticationResponse refresh(RefreshRequest refreshRequest) {
        String token = refreshRequest.getRefreshToken();
        String username = jwtTokenProvider.extractUsername(token);

        AppUser appUser = findUserByEmail(username);

        if (!token.equals(appUser.getRefreshToken())) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        return generateTokensAndSave(appUser);
    }

    public AuthenticationResponse confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Confirmation Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new EmailAlreadyConfirmedException("Email already confirmed");
        }
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);

        AppUser appUser = confirmationToken.getAppUser();
        appUser.setEnabled(true);

        return generateTokensAndSave(appUser);
    }

    // private methods for help

    private AppUser findUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private String createAndSaveConfirmationToken(AppUser user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(emailTokenExpiration)
        );
        confirmationTokenRepository.save(confirmationToken);
        return token;
    }

    private AuthenticationResponse generateTokensAndSave(AppUser user) {
        AppUserDetails userDetails = new AppUserDetails(user);
        String jwtToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        user.setRefreshToken(refreshToken);
        appUserRepository.save(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String buildEmail(String name, String link) {
        return "<div>" +
                "Hi " + name + ",<br>" +
                "Please click on the below link to activate your account:<br>" +
                "<a href=\"" + link + "\">Activate Now</a>" +
                "<br>This link will expire in 15 minutes.<br>" +
                "See you soon!" +
                "</div>";
    }
}

