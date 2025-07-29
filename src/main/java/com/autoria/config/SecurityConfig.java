package com.autoria.config;

import com.autoria.filters.JwtAuthenticationFilter;
import com.autoria.security.user.AppUserDetailService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final AppUserDetailService appUserDetailService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(appUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Всі можуть реєструватись та логінитись
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/listings").permitAll() // загальний список всіх оголошень
                        .requestMatchers(HttpMethod.GET, "/api/v1/listings/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/listings/filter").permitAll()
                        .requestMatchers("/api/v1/listings/my/**").hasAnyRole("SELLER", "MANAGER", "ADMIN") // захист для особистих оголошень
                        .requestMatchers("/api/v1/listings/**").hasAnyRole("SELLER", "MANAGER", "ADMIN")
                        // інші операції з оголошеннями

                        // Менеджери і адміни можуть керувати користувачами
                        .requestMatchers("/api/v1/users/ban/**").hasAnyRole("MANAGER", "ADMIN")

                        // Адмін може все інше
                        .anyRequest().hasRole("ADMIN")
                )

                // Додаємо обробники помилок:
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("""
                    {
                      "error": "Forbidden",
                      "message": "Access is denied"
                    }
                    """);
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("""
                    {
                      "error": "Unauthorized",
                      "message": "Authentication required"
                    }
                    """);
                        })
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Без сесій (JWT)
                .authenticationProvider(authenticationProvider()) // Вказуємо власний провайдер аутентифікації
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Додаємо JWT фільтр

        return http.build();
    }
}
