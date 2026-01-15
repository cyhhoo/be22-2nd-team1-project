package com.mycompany.project.config;

import com.mycompany.project.jwtsecurity.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나, 익명 사용자인 경우 빈값 반환
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return Optional.empty(); // 로그인하지 않은 경우 (null 처리)
        }

        // CustomUserDetails 인 경우 이메일 반환, 나중에 email 말고 다른 정보 필요한경우 수정 가능하도록 지금 구조로 둠
        if (authentication.getPrincipal() instanceof CustomUserDetails){
          return Optional.of(((CustomUserDetails) authentication.getPrincipal()).getEmail());
        }

        return Optional.of(authentication.getName()); // 사용자 이메일 반환
    }
}