package com.autoria.filters;

import com.autoria.models.user.AppUser;
import com.autoria.repository.AppUserRepository;
import com.autoria.security.jwt.JwtTokenProvider;
import com.autoria.security.user.AppUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AppUserRepository appUserRepository;

    private final AppUserDetailService appUserDetailService;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return; // пропускаємо без авторизації
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String userEmail;

        try {
            userEmail = jwtTokenProvider.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            response.setHeader("Error", "Token is expired");
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            response.setHeader("Error", "Invalid token");
            filterChain.doFilter(request, response);
            return;
        }


        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = appUserDetailService.loadUserByUsername(userEmail);

            AppUser appUser = appUserRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

            if (jwtTokenProvider.isTokenValid(jwt, userDetails) && !jwt.equals(appUser.getRefreshToken())) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        // ----------------
        filterChain.doFilter(request, response);
    }
}
