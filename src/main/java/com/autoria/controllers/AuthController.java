package com.autoria.controllers;

import com.autoria.models.authentication.AuthenticationRequest;
import com.autoria.models.authentication.AuthenticationResponse;
import com.autoria.models.refresh.RefreshRequest;
import com.autoria.models.register.RegisterRequest;
import com.autoria.models.register.RegisterResponse;
import com.autoria.services.authentication.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.accepted().body(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authenticationService.refresh(refreshRequest));
    }

    @GetMapping("/confirm")
    public ResponseEntity<AuthenticationResponse> confirm(@RequestParam String token) {
        AuthenticationResponse result = authenticationService.confirmEmail(token);
        return ResponseEntity.ok(result);
    }
}
