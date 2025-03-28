package com.adarsh.Ecommerce.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails
{

    private final User user;
    public UserPrincipal(User user)
    {
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getRole();
        if (role == null) {
            System.out.println("UserPrincipal - Role is null for user: " + user.getUsername());
            return Collections.emptyList();
        }
        if (role.startsWith("ROLE_")) {
            System.out.println("UserPrincipal - Role already prefixed: " + role);
        } else {
            role = "ROLE_" + role;
            System.out.println("UserPrincipal - Role prefixed: " + role);
        }
        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(role));
        System.out.println("UserPrincipal - Authorities: " + authorities);
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
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
