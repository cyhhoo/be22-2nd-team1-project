package com.mycompany.project.auth.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Builder
    public Token(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
