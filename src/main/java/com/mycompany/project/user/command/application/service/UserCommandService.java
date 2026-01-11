package com.mycompany.project.user.command.application.service;

import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.user.command.domain.aggregate.*;
import com.mycompany.project.user.command.domain.repository.*;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserCommandService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final StudentDetailRepository studentDetailRepository;
  private final TeacherDetailRepository teacherDetailRepository;
  private final SubjectRepository subjectRepository;
  private final AdminDetailRepository adminDetailRepository;

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

          // 역할별 상세 정보 저장
          switch (role) {
            case STUDENT:
              // 1. CSV에서 학생 상세 정보 관련 정보 추출
              String gradeStr = getCsvValue(data, headers, "grade");
              String classNo = getCsvValue(data, headers, "classNo");
              String studentNoStr = getCsvValue(data, headers, "studentNo");

              // 2. 추출한 데이터 파싱
              Integer grade = null;
              Integer studentNo = null;
              if (gradeStr != null) {
                try {
                  grade = Integer.parseInt(gradeStr);
                } catch (NumberFormatException e) {
                  // ignore
                }
              }
              if (studentNoStr != null) {
                try {
                  studentNo = Integer.parseInt(studentNoStr);
                } catch (NumberFormatException e) {
                  // ignore
                }
              }

              // 3. 엔티티 생성 및 저장
              StudentDetail studentDetail = StudentDetail.builder()
                  .user(user)
                  .grade(grade)
                  .classNo(classNo)
                  .studentNo(studentNo)
                  .build();

              studentDetailRepository.save(studentDetail);
              break;

            case TEACHER:
              // 1. CSV에서 교사 관련 데이터 추출
              String subjectName = getCsvValue(data, headers, "subject");
              String homeroomGradeStr = getCsvValue(data, headers, "homeroomGrade");
              String homeroomClassStr = getCsvValue(data, headers, "homeroomClass");

              // 2. 과목 조회
              Subject subject = null;
              if (subjectName != null && !subjectName.isEmpty()) {
                subject = subjectRepository.findByName(subjectName).orElse(null);
              }
              // 3. 파싱
              Integer hrGrade = null;
              Integer hrClass = null;

              if (homeroomGradeStr != null) {
                try {
                  hrGrade = Integer.parseInt(homeroomGradeStr);
                } catch (NumberFormatException e) {
                  // ignore
                }
              }
              if (homeroomClassStr != null) {
                try {
                  hrClass = Integer.parseInt(homeroomClassStr);
                } catch (NumberFormatException e) {
                  // ignore
                }
              }

              // 4. TeacherDetail 생성 및 저장
              TeacherDetail teacherDetail = TeacherDetail.builder()
                  .user(user)
                  .subject(subject)
                  .homeroomGrade(hrGrade)
                  .homeroomClassNo(hrClass)
                  .build();
              teacherDetailRepository.save(teacherDetail);
              break;

            case ADMIN:
              // 1. CSV에서 관리자 관련 데이터 추출
              String levelStr = getCsvValue(data, headers, "adminLevel"); // 예: 1 or 5

              // 2. Level 매핑 (기본값 설정 로직 등 필요)
              AdminDetail.AdminLevel level = AdminDetail.AdminLevel.LEVEL_5; // 기본값
              if ("1".equals(levelStr)) {
                level = AdminDetail.AdminLevel.LEVEL_1;
              }

              // 3. AdminDetail 생성 및 저장
              AdminDetail adminDetail = AdminDetail.builder()
                  .user(user)
                  .level(level)
                  .build();
              adminDetailRepository.save(adminDetail);
              break;
          }

        }

      }
      return count;
    }

    catch (IOException e) {
      throw new RuntimeException("CSV 파일 읽기 실패");
    }
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
