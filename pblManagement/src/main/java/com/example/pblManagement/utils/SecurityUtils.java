package com.example.pblManagement.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    public String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getId();
        }

        throw new IllegalStateException("Current user not found in security context");
    }

    public String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getUsername();
        } else if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getUsername();
        }

        return principal.toString();
    }
}
