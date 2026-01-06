package com.mycompany.project.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "tbl_token")
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tokenId;

  @Column(nullable = false, unique = true)
  private String token; // Refresh Token 값
  @Column(nullable = false)

  private String email; // 누구의 토큰인지 (User 연관관계를 맺어도 되지만, 간단히 이메일로 저장)

  // 만료 시간 등은 JWT 자체 claim을 활용하거나 필요 시 필드 추가

}
