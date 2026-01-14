package com.mycompany.project.user.query.dto;

import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

  private Long userId;
  private String email;
  private String name;
  private Role role;
  private UserStatus status;
  private LocalDate birthDate;

}
