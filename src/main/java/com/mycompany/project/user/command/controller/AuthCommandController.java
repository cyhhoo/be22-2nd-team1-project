package com.mycompany.project.user.command.controller;

import com.mycompany.project.common.dto.ApiDTO;
import com.mycompany.project.user.command.dto.AccountActivationRequest;
import com.mycompany.project.user.command.dto.UserRegisterRequest;
import com.mycompany.project.user.command.service.UserCommandService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // URL은 유지
public class AuthCommandController {

  private final UserCommandService userCommandService;

  public AuthCommandController(UserCommandService userCommandService) {
    this.userCommandService = userCommandService;
  }

  @PostMapping("/register")
  public ApiDTO<Long> register(@RequestBody UserRegisterRequest request) {
    return ApiDTO.success(userCommandService.registerUser(request));
  }

  @PostMapping("/activate")
  public ApiDTO<Void> activate(@RequestBody AccountActivationRequest request) {
    userCommandService.activateAccount(request);
    return ApiDTO.success(null);
  }

}