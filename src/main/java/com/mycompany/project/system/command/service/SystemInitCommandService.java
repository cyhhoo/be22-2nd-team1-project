package com.mycompany.project.system.command.service;

import com.mycompany.project.user.entity.Role;
import com.mycompany.project.user.entity.User;
import com.mycompany.project.user.entity.UserStatus;
import com.mycompany.project.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

@Service
public class SystemInitCommandService {
    // Inject other services or repositories as needed
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public SystemInitCommandService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * CSV 파일 읽어서 인원 대량 등록 하는 메서드
   * @param csvContent
   */
  public void importUserInBatch(String csvContent){

    try (BufferedReader br = new BufferedReader(new StringReader(csvContent))){
      String line;

      while((line = br.readLine()) != null){
        String[] data = line.split(",");

        if (data.length < 5){ // user의 column에 들어갈 값이 최소 5개(email,password,name,role,birthDate), 부족하면 넘어감
          continue;
        }
        String email = data[0].trim();
        String password = data[1].trim();
        String name = data[2].trim();
        String roleStr = data[3].trim();
        String birthDate = data[4].trim();

        if (userRepository.existsByEmail(email)){ // 이미 등록된 이메일이라면
          continue;
        }

        Role role = Role.valueOf(roleStr.toUpperCase()); // Enumdmf qusghks

        // User 엔티티 생성
        User user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name(name)
            .role(role)
            .status(UserStatus.INACTIVE)
            .birthDate(birthDate)
            .authCode(birthDate.substring(2))
            .build();

        // 저장
        userRepository.save(user);
      }

    }
    catch (IOException e){
      throw new RuntimeException("CSV 파일 읽기 실패", e);
    }
  }
}
