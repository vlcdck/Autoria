package com.autoria.security;

import com.autoria.models.user.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AppUserDetails implements UserDetails {

    private final AppUser appUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return appUser.getRoles().stream()
                .map(role -> "ROLE_" + role.getName().name())
                .map(roleName -> (GrantedAuthority) () -> roleName)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
