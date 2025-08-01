package com.autoria.security.user;

import com.autoria.models.user.AppUser;
import com.autoria.models.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class AppUserDetails implements UserDetails {

    private final AppUser appUser;

    public UUID getId() {
        return appUser.getId();
    }

    public Set<Role> getRoles() {
        return appUser.getRoles();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (appUser.getRoles() == null) return Collections.emptySet();

        return appUser.getRoles().stream()
                .flatMap(role -> {

                    Set<GrantedAuthority> authorities = role.getPermissions().stream()

                            .map(permission -> (GrantedAuthority) permission::getCode)
                            .collect(Collectors.toSet());


                    authorities.add((GrantedAuthority) () -> "ROLE_" + role.getName().name());

                    return authorities.stream();
                })
                .collect(Collectors.toSet());
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
        return true; // можна замінити на appUser.isAccountNonExpired() якщо є
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // аналогічно – можеш додати поле в AppUser якщо потрібно
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return appUser.isEnabled();
    }


}
