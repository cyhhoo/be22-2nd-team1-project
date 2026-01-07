package com.mycompany.project.user.command.service;

import com.mycompany.project.user.command.dto.AccountActivationRequest;
import com.mycompany.project.user.command.dto.UserRegisterRequest;
import com.mycompany.project.user.entity.Role;
import com.mycompany.project.user.entity.User;
import com.mycompany.project.user.entity.UserStatus;
import com.mycompany.project.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.project.common.aop.SystemLoggable;
import com.mycompany.project.common.entity.ChangeType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserCommandService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserCommandService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * 유저 개별 등록 하는 메서드
   * 
   * @param request
   * @return
   */
  @Transactional
  @SystemLoggable(type = ChangeType.CREATE, tableCodeId = 1) // 1: tbl_user 임시 ID
  public Long registerUser(UserRegisterRequest request) {
    // 1. 이메일 중복 검사
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    // 2. 초기 상태 설정 (일단 INACTIVE로 시작)
    UserStatus initialStatus = UserStatus.INACTIVE;

    // 3. User 엔티티 생성 (Builder 패턴 사용)
    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화 필수!
        .name(request.getName())
        .role(request.getRole())
        .status(initialStatus)
        .birthDate(request.getBirthDate())
        .authCode(request.getBirthDate().substring(2)) // 생년월일 6자리로 임시 인증코드 생성
        .build();

    // 4. 저장
    return userRepository.save(user).getUserId();
  }

  @Transactional
  public int importUser(MultipartFile file) {
    try {
      String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
      int count = 0;
      try (BufferedReader br = new BufferedReader(new StringReader(csvContent))) {
        // CSV 헤더 읽기
        String headerLine = br.readLine();
        if (headerLine == null)
          return 0; // 헤더 없으면 빈 파일이라 0 반환

        Map<String, Integer> headers = parseHeaders(headerLine);

        if (!(headers.containsKey("email") && headers.containsKey("password"))) {
          throw new IllegalArgumentException("필수 컬럼(email, password)이 누락되었습니다.");
        }
        // 데이터 추출
        String line;
        while ((line = br.readLine()) != null) {
          if (line.isEmpty())
            continue;

          String[] data = line.split(",");
          if (data.length < 5)
            continue;

          String email = getCsvValue(data, headers, "email");
          if (email == null || userRepository.existsByEmail(email))
            continue;

          String name = getCsvValue(data, headers, "name");
          String password = getCsvValue(data, headers, "password");
          String roleStr = getCsvValue(data, headers, "roleStr");
          String birthDate = getCsvValue(data, headers, "birthDate");

          // 엔티티 저장

          // Role 변환
          Role role;
          try {
            // 입력값이 "ROLE_" 로 시작한다면 제거해주는 등의 전처리 가능
            String roleInput = roleStr.toUpperCase().replace("ROLE_", "");
            role = Role.valueOf(roleInput);
          } catch (IllegalArgumentException | NullPointerException e) {
            role = Role.STUDENT; // 기본값
          }

          // 인증코드 생성 (하이픈 제거 후 뒤 6자리)
          // 2005-01-01 -> 050101
          String authCode = null;
          if (birthDate != null) {
            String cleanDate = birthDate.replace("-", ""); // 20050101
            if (cleanDate.length() >= 6) {
              authCode = cleanDate.substring(cleanDate.length() - 6);
            }
          }
          User user = User.builder()
              .email(email)
              .password(passwordEncoder.encode(password))
              .name(name)
              .role(role)
              .birthDate(birthDate)
              .authCode(authCode)
              .status(UserStatus.INACTIVE)
              .build();
          userRepository.save(user);

          count++;
        }

      }
      return count;
    }

    catch (IOException e) {
      throw new RuntimeException("CSV 파일 읽기 실패");
    }
  }

  public void activateAccount(AccountActivationRequest request) {
    // 1. 사용자 조회
    User user = userRepository.findByEmail(request.getEmail())
        // .orElseThrow(IllegalArgumentException::new);
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사람 입니다."));

    // 2. 이미 활성화 된 경우. (아마 없을거 같긴 한데)
    if (user.getStatus() == UserStatus.ACTIVE) {
      throw new IllegalArgumentException("이미 활성화 된 계정입니다.");
    }

    // 3. 본인 인증
    if (!(user.getName().equals(request.getName()) && user.getBirthDate().equals(request.getBirthDate()))) {
      throw new IllegalArgumentException("사용자 정보가 일치하지 않습니다.");
    }

    // 인증 코드 체크
    if (user.getAuthCode() != null && !user.getAuthCode().equals(request.getAuthCode())) {
      throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
    }

    // 4. 활성화
    user.activate(passwordEncoder.encode(request.getNewPassword()));

    // 5. 저장
    userRepository.save(user);
  }

  /**
   * CSV 파일 Header Index Mapping 헬퍼 메서드
   * 
   * @param headerLine
   * @return
   */
  private Map<String, Integer> parseHeaders(String headerLine) {
    Map<String, Integer> map = new HashMap<>();
    String[] cols = headerLine.split(",");
    for (int i = 0; i < cols.length; i++) {
      // "Email", " email " 등 정규화
      map.put(cols[i].trim().toLowerCase(), i);
    }
    return map;
  }

  /**
   * CSV Header 값 안전하게 가져오는 헬퍼 메서드
   * 
   * @param data
   * @param headers
   * @param columnName
   * @return
   */
  private String getCsvValue(String[] data, Map<String, Integer> headers, String columnName) {
    String key = columnName.toLowerCase();
    if (!headers.containsKey(key))
      return null;

    int index = headers.get(key);
    if (index >= data.length)
      return null; // 데이터가 부족한 경우

    return data[index].trim();
  }

  @Transactional
  public int updateUsersInBatch(MultipartFile file) {
    try {
      String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
      int count = 0;
      try (BufferedReader br = new BufferedReader(new StringReader(csvContent))) {
        String headerLine = br.readLine();
        if (headerLine == null)
          return 0;

        Map<String, Integer> headers = parseHeaders(headerLine);
        if (!headers.containsKey("email")) {
          throw new IllegalArgumentException("식별자(email) 컬럼이 필요합니다.");
        }

        String line;
        while ((line = br.readLine()) != null) {
          if (line.isEmpty())
            continue;
          String[] data = line.split(",");

          String email = getCsvValue(data, headers, "email");
          if (email == null)
            continue;

          User user = userRepository.findByEmail(email).orElse(null);
          if (user == null)
            continue;

          String name = getCsvValue(data, headers, "name");
          String roleStr = getCsvValue(data, headers, "role");
          String birthDate = getCsvValue(data, headers, "birthDate");

          Role role = null;
          if (roleStr != null) {
            try {
              String roleInput = roleStr.toUpperCase().replace("ROLE_", "");
              role = Role.valueOf(roleInput);
            } catch (IllegalArgumentException e) {
              // ignore
            }
          }

          user.updateBatchInfo(name, role, birthDate);

          // Dirty Checking으로 자동 저장됨
          count++;
        }
      }
      return count;
    } catch (IOException e) {
      throw new RuntimeException("CSV 처리 실패", e);
    }
  }
}
