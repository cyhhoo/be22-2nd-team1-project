package com.mycompany.project.security;

import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private final Long userId; // PK 異붽?
    private final String email;
    private final Role role;
    private final UserStatus status;

    // --- UserDetails ?꾩닔 援ы쁽 硫붿꽌??---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    } // Stateless ?섎?濡?鍮꾨?踰덊샇???꾩슂 ?놁쓬

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

}
