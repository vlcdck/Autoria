package com.autoria.security.util;

import com.autoria.models.user.AppUser;
import com.autoria.security.user.AppUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtil {

    public static UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserDetails)) {
            throw new IllegalStateException("No authenticated user found");
        }
        return ((AppUserDetails) authentication.getPrincipal()).getAppUser();
    }

}
