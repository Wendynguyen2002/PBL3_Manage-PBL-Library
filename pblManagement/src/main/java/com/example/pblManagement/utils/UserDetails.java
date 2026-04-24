package com.example.pblManagement.utils;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface UserDetails {
    String getUsername();

    String getPassword();

    Collection<? extends GrantedAuthority> getAuthorities();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
