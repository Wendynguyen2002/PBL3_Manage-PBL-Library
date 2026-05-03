package com.example.pblManagement.utils;

import com.example.pblManagement.model.entities.enums.UserRole;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final String id;
    private final String email;
    private final String fullName;
    private final String password;
    private final UserRole role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String email, String fullName, String password,
                           UserRole role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public @NonNull String getUsername() {
        return email; // Return email for authentication
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isLecturer() {
        return role == UserRole.LECTURER;
    }

    public boolean isStudent() {
        return role == UserRole.STUDENT;
    }

    public String getRoleString() {
        return "ROLE_" + role.name();
    }
}
