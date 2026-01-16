package com.mycompany.project.user.configuration;

import com.mycompany.project.user.command.domain.aggregate.AdminDetail;
import com.mycompany.project.user.command.domain.aggregate.AdminLevel;
import com.mycompany.project.common.enums.Role;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.common.enums.UserStatus;
import com.mycompany.project.user.command.domain.repository.AdminDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * DataInitializer class to set up initial data on application startup
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminDetailRepository adminDetailRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Create initial admin account only if no users exist
        if (userRepository.count() == 0) {
            log.info("Creating initial system admin account...");

            User admin = User.builder()
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .name("System Admin")
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE) // Set to ACTIVE immediately for initial admin
                    .birthDate(LocalDate.parse("1990-01-01"))
                    .build();

            userRepository.save(java.util.Objects.requireNonNull(admin));

            // Create AdminDetail (Set to LEVEL_1)
            AdminDetail adminDetail = AdminDetail.builder()
                    .user(admin)
                    .level(AdminLevel.LEVEL_1)
                    .build();
            adminDetailRepository.save(java.util.Objects.requireNonNull(adminDetail));

            log.info("Admin account created successfully: admin@test.com / admin1234");
        } else {
            log.info("User data already exists. Skipping initialization.");
        }
    }
}
