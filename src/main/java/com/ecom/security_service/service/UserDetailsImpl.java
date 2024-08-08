package com.ecom.security_service.service;

import com.ecom.security_service.dao.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private final User internalUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return internalUser.getPassword();
    }

    @Override
    public String getUsername() {
        return internalUser.getEmail();
    }
}
