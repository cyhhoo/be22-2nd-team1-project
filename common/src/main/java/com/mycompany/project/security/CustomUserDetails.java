package com.mycompany.project.security;

import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
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

    private final Long userId; // PK 추가
    private final String email;
    private final Role role;
    private final UserStatus status;

    // --- UserDetails 필수 구현 메서드 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    } // Stateless 하므로 비밀번호는 필요 없음

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
