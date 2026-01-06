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

  /**
   * 초기 계정 활성화 메서드
   * 계정 생성 후 첫 로그인 시, 동작
   * @param encodedPassword
   */
  public void activate(String encodedPassword) {
    this.status = UserStatus.ACTIVE;
    this.password = encodedPassword;
    this.loginFailCount = 0;
  }

  /**
   * 로그인 성공 시, 카운트 초기화 및 마지막 접속일 갱신
   */
  public void loginSuccess(){
    this.loginFailCount = 0;
    this.lastLoginAt = LocalDateTime.now();
  }

  /**
   * 로그인 실패 시, 실패 카운트 증가 및 5회 이상 실패시 계정 잠금
   */
  public void loginFail(){
    this.loginFailCount++;
    if(this.loginFailCount >=5){
      this.status = UserStatus.LOCKED;
    }
  }

  /**
   * 계정 잠금 상태 확인 메서드
   * @return 잠금 상태 여부 (LOCKED 면 true, 아니면 false)
   */
  public boolean isLocked(){
    return this.status == UserStatus.LOCKED;
  }
}
