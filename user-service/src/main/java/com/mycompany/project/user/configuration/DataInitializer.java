package com.mycompany.project.user.configuration;

import com.mycompany.project.user.command.domain.aggregate.AdminDetail;
import com.mycompany.project.user.command.domain.aggregate.AdminLevel;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import com.mycompany.project.user.command.domain.repository.AdminDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 애플리케이션 기동 시 초기 데이터를 설정하는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminDetailRepository adminDetailRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 유저가 한 명도 없는 경우에만 초기 관리자 계정 생성
        if (userRepository.count() == 0) {
            log.info("초기 시스템 관리자 계정을 생성합니다...");

            User admin = User.builder()
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .name("시스템관리자")
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE) // 최초 관리자는 바로 활성 상태로 생성
                    .birthDate(LocalDate.parse("1990-01-01"))
                    .build();

            userRepository.save(admin);

            // 관리자 상세 정보 생성 (LEVEL_1로 설정)
            AdminDetail adminDetail = AdminDetail.builder()
                    .user(admin)
                    .level(AdminLevel.LEVEL_1)
                    .build();
            adminDetailRepository.save(adminDetail);

            log.info("관리자 계정 생성 완료: admin@test.com / admin1234");
        } else {
            log.info("기존 유저 데이터가 존재하여 초기화를 건너뜜");
        }
    }
}
