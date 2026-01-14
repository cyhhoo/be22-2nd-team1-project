package com.mycompany.project.auth.command.infrastructure;

import com.mycompany.project.auth.client.dto.UserInternalActivateRequest;
import com.mycompany.project.auth.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "swcamp-user-service", url = "${gateway.url}")
public interface UserClient {

    @GetMapping("/user/internal/email/{email}")
    UserResponse getByEmail(@PathVariable("email") String email);

    @PostMapping("/user/internal/activate")
    void activateUser(@RequestBody UserInternalActivateRequest request);
}
