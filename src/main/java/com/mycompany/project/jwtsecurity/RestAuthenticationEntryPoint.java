package com.mycompany.project.jwtsecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.project.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/* 인증 실패(401) 핸들러 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

      ApiResponse<Void> apiResponse = ApiResponse.failure("Unauthorized","인증이 필요합니다" + authException.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}