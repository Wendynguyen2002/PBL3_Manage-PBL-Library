package com.example.pblManagement.utils;

import com.example.pblManagement.model.entities.enums.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    public UserDetailsImpl getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl) {
            return (UserDetailsImpl) principal;
        }

        throw new IllegalStateException("Current user details not found");
    }

    public String getCurrentUserId() {
        return getCurrentUserDetails().getId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUserDetails().getEmail();
    }

    public UserRole getCurrentUserRole() {
        return getCurrentUserDetails().getRole();
    }

    public void verifyRole(UserRole expectedRole) {
        UserRole actualRole = getCurrentUserRole();
        if (expectedRole != actualRole) {
            throw new AccessDeniedException("Required role: " + expectedRole + ", but was: " + actualRole);
        }
    }

    public void verifyAdmin() {
        verifyRole(UserRole.ADMIN);
    }

    public void verifyLecturer() {
        verifyRole(UserRole.LECTURER);
    }

    public void verifyStudent() {
        verifyRole(UserRole.STUDENT);
    }
}

