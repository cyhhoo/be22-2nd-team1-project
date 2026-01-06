package com.mycompany.project.user.repository;

import com.mycompany.project.user.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    boolean existsByToken(String token);
    void deleteByEmail(String email); // 기존 토큰 삭제용
}