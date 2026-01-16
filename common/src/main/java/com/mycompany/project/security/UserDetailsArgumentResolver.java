package com.mycompany.project.security;

import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserDetailsArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CustomUserDetails.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String userId = webRequest.getHeader("X-User-Id");
        String email = webRequest.getHeader("X-User-Email");
        String roleStr = webRequest.getHeader("X-User-Role");
        String statusStr = webRequest.getHeader("X-User-Status");

        if (userId == null || email == null) {
            return null;
        }

        return CustomUserDetails.builder()
                .userId(Long.valueOf(userId))
                .email(email)
                .role(roleStr != null ? Role.valueOf(roleStr) : null)
                .status(statusStr != null ? UserStatus.valueOf(statusStr) : null)
                .build();
    }
}
