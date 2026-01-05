package com.mycompany.project.user.entity;

import com.mycompany.project.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_user")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true, nullable = false, length = 100)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, length = 50)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = false)
  private String birthDate;

  private String authCode; // 인증 코드

  private int loginFailCount; // 로그인 실패 횟수 (5회 실패시 계정 잠금)

  private LocalDateTime lastLoginAt; // 마지막 로그인 일시

}
