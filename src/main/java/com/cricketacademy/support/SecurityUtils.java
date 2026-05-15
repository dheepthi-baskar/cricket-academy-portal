package com.cricketacademy.support;

import com.cricketacademy.security.AcademyUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static AcademyUserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AcademyUserDetails user)) {
            throw new AccessDeniedException("Authentication required");
        }
        return user;
    }
}
