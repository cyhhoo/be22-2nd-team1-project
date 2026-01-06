package com.mycompany.project.user.command.controller;

import com.mycompany.project.common.dto.ApiDTO;
import com.mycompany.project.user.command.dto.SignupRequest;
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

  @PostMapping("/signup")
  public ApiDTO<Long> signup(@RequestBody SignupRequest request) {
    return ApiDTO.success(userCommandService.signup(request));
  }
}