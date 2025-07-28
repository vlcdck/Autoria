package com.autoria.security.jwt;

import com.autoria.models.user.Permission;
import com.autoria.security.user.AppUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private Key signingKey;

    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    private String buildToken(Map<String, Object> claims, String subject, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateAccessToken(UserDetails userDetails) {
        AppUserDetails appUserDetails = (AppUserDetails) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", appUserDetails.getId());
        claims.put("email", appUserDetails.getUsername());

        // Collecting all roles
        Set<String> roles = appUserDetails.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        claims.put("roles", roles);

        // Collecting all permissions from roles
        Set<String> permissions = appUserDetails.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        claims.put("permissions", permissions);
        claims.put("authorities", permissions);

        return buildToken(claims, appUserDetails.getUsername(), accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails.getUsername(), refreshTokenExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
