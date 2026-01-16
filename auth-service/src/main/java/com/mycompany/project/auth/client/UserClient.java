package com.mycompany.project.auth.client;

import com.mycompany.project.common.dto.UserAuthResponse;
import com.mycompany.project.common.dto.UserInternalActivateRequest;
import com.mycompany.project.common.dto.UserInternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "swcamp-user-service")
public interface UserClient {

    @GetMapping("/api/v1/internal/users/auth")
    UserAuthResponse getUserForAuth(@RequestParam("email") String email);

    @GetMapping("/api/v1/internal/users/email")
    UserInternalResponse getByEmail(@RequestParam("email") String email);

    @PostMapping("/api/v1/internal/users/activate")
    void activateUser(@RequestBody UserInternalActivateRequest request);

    @PostMapping("/api/v1/internal/users/login-success")
    void recordLoginSuccess(@RequestParam("email") String email);

    @PostMapping("/api/v1/internal/users/login-fail")
    void recordLoginFail(@RequestParam("email") String email);
}
