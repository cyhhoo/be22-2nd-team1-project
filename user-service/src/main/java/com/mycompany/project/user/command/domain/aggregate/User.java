package com.mycompany.project.user.command.domain.aggregate;

import com.mycompany.project.common.entity.BaseEntity;
import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @NotNull
    @Column(nullable = false)
    private LocalDate birthDate;

    private String authCode; // Verification code

    private int loginFailCount; // Login failure count (Locked after 5 failures)

    private LocalDateTime lastLoginAt; // Last login timestamp

    /**
     * Account activation method
     * Executed on first login after account creation
     * 
     * @param encodedPassword Encoded new password
     */
    public void activate(String encodedPassword) {
        this.status = UserStatus.ACTIVE;
        this.password = encodedPassword;
        this.loginFailCount = 0;
    }

    /**
     * Reset fail count and update last login timestamp on success
     */
    public void loginSuccess() {
        this.loginFailCount = 0;
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * Increase fail count and lock account if 5 or more failures
     */
    public void loginFail() {
        this.loginFailCount++;
        if (this.loginFailCount >= 5) {
            this.status = UserStatus.LOCKED;
        }
    }

    /**
     * Check if account is locked
     * 
     * @return true if status is LOCKED
     */
    public boolean isLocked() {
        return this.status == UserStatus.LOCKED;
    }

    public void updateBatchInfo(String name, Role role, LocalDate birthDate) {
        if (name != null && !name.isEmpty())
            this.name = name;
        if (role != null)
            this.role = role;
        if (birthDate != null)
            this.birthDate = birthDate;
    }

    /**
     * Update password
     * 
     * @param encodedPassword Encoded new password
     */
    public void setPassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
