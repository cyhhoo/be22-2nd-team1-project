package com.mycompany.project.user.command.application.service;

import com.mycompany.project.common.aop.SystemLoggable;
import com.mycompany.project.common.entity.BulkUploadLog;
import com.mycompany.project.common.entity.ChangeType;
import com.mycompany.project.common.entity.UploadStatus;
import com.mycompany.project.common.entity.UploadType;
import com.mycompany.project.common.repository.BulkUploadLogRepository;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.user.command.application.dto.*;
import com.mycompany.project.user.command.domain.aggregate.*;
import com.mycompany.project.user.command.domain.repository.*;
import com.mycompany.project.schedule.command.domain.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
  private final BulkUploadLogRepository bulkUploadLogRepository;

  /**
   * 유저 개별 등록 하는 메서드
   * - 역할에 따라 StudentDetail, TeacherDetail, AdminDetail 자동 생성
   */
  @Transactional
  @SystemLoggable(type = ChangeType.CREATE, tableCodeId = 1)
  public Long registerUser(UserRegisterRequest request) {
    // 1. 이메일 중복 검사
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    // 2. 초기 상태 설정 (INACTIVE로 시작)
    UserStatus initialStatus = UserStatus.INACTIVE;

    // 3. User 엔티티 생성
    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .name(request.getName())
        .role(request.getRole())
        .status(initialStatus)
        .birthDate(request.getBirthDate())
        .authCode(request.getBirthDate() != null
            ? request.getBirthDate().format(DateTimeFormatter.ofPattern("yyMMdd"))
            : null)
        .build();

    // 4. User 저장
    userRepository.save(user);

    // 5. 역할별 상세 정보 생성
    createRoleDetailFromRequest(user, request);

    return user.getUserId();
  }

  /**
   * 역할에 따라 StudentDetail, TeacherDetail, AdminDetail 생성 (개별 등록용)
   */
  private void createRoleDetailFromRequest(User user, UserRegisterRequest request) {
    switch (user.getRole()) {
      case STUDENT:
        createStudentDetailFromRequest(user, request.getStudentDetail());
        break;
      case TEACHER:
        createTeacherDetailFromRequest(user, request.getTeacherDetail());
        break;
      case ADMIN:
        createAdminDetailFromRequest(user, request.getAdminDetail());
        break;
    }
  }

  private void createStudentDetailFromRequest(User user, StudentDetailRequest detailReq) {
    StudentDetail.StudentDetailBuilder builder = StudentDetail.builder().user(user);

    if (detailReq != null) {
      builder.grade(detailReq.getGrade())
          .classNo(detailReq.getClassNo())
          .studentNo(detailReq.getStudentNo());
    }

    studentDetailRepository.save(builder.build());
  }

  private void createTeacherDetailFromRequest(User user, TeacherDetailRequest detailReq) {
    TeacherDetail.TeacherDetailBuilder builder = TeacherDetail.builder().user(user);

    if (detailReq != null) {
      // 과목 정보 설정
      Subject subject = null;
      if (detailReq.getSubjectId() != null) {
        subject = subjectRepository.findById(detailReq.getSubjectId()).orElse(null);
      } else if (detailReq.getSubject() != null && !detailReq.getSubject().isEmpty()) {
        subject = subjectRepository.findByName(detailReq.getSubject()).orElse(null);
      }

      builder.subject(subject)
          .homeroomGrade(detailReq.getHomeroomGrade())
          .homeroomClassNo(detailReq.getHomeroomClass());
    }

    teacherDetailRepository.save(builder.build());
  }

  private void createAdminDetailFromRequest(User user, AdminDetailRequest detailReq) {
    AdminLevel level = AdminLevel.LEVEL_5; // 기본값

    if (detailReq != null && "1".equals(detailReq.getLevel())) {
      level = AdminLevel.LEVEL_1;
    }

    AdminDetail adminDetail = AdminDetail.builder()
        .user(user)
        .level(level)
        .build();

    adminDetailRepository.save(adminDetail);
  }

  @Transactional
  public int importUser(MultipartFile file) {
    // 1. BulkUploadLog 생성 (PENDING 상태)
    BulkUploadLog uploadLog = BulkUploadLog.builder()
        .uploadType(UploadType.USER_REG)
        .status(UploadStatus.PENDING)
        .build();
    bulkUploadLogRepository.save(uploadLog);

    // 2. 처리 시작
    uploadLog.startProcessing();

    int successCount = 0;
    int failCount = 0;
    StringBuilder errorLog = new StringBuilder();

    try {
      String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
      try (BufferedReader br = new BufferedReader(new StringReader(csvContent))) {
        // CSV 헤더 읽기
        String headerLine = br.readLine();
        if (headerLine == null) {
          uploadLog.complete(0, 0, "Empty file");
          return 0;
        }

        Map<String, Integer> headers = parseHeaders(headerLine);

        if (!(headers.containsKey("email") && headers.containsKey("password"))) {
          uploadLog.fail("필수 컬럼(email, password)이 누락되었습니다.");
          throw new IllegalArgumentException("필수 컬럼(email, password)이 누락되었습니다.");
        }

        // 데이터 추출
        String line;
        int lineNum = 1;
        while ((line = br.readLine()) != null) {
          lineNum++;
          if (line.isEmpty())
            continue;

          try {
            String[] data = line.split(",");
            if (data.length < 5) {
              failCount++;
              errorLog.append("Line ").append(lineNum).append(": 데이터 부족\n");
              continue;
            }

            String email = getCsvValue(data, headers, "email");
            if (email == null || userRepository.existsByEmail(email)) {
              failCount++;
              errorLog.append("Line ").append(lineNum).append(": 이메일 중복 또는 누락\n");
              continue;
            }

            String name = getCsvValue(data, headers, "name");
            String password = getCsvValue(data, headers, "password");
            String roleStr = getCsvValue(data, headers, "roleStr");
            String birthDateStr = getCsvValue(data, headers, "birthDate");

            // Role 변환
            Role role;
            try {
              String roleInput = roleStr.toUpperCase().replace("ROLE_", "");
              role = Role.valueOf(roleInput);
            } catch (IllegalArgumentException | NullPointerException e) {
              role = Role.STUDENT; // 기본값
            }

            // birthDate 파싱 (LocalDate)
            LocalDate birthDate = null;
            String authCode = null;
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
              try {
                birthDate = LocalDate.parse(birthDateStr); // yyyy-MM-dd 형식
                authCode = birthDate.format(DateTimeFormatter.ofPattern("yyMMdd"));
              } catch (Exception e) {
                // 파싱 실패 시 null 유지
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

            successCount++;

            // 역할별 상세 정보 저장
            switch (role) {
              case STUDENT:
                saveStudentDetail(user, data, headers);
                break;
              case TEACHER:
                saveTeacherDetail(user, data, headers);
                break;
              case ADMIN:
                saveAdminDetail(user, data, headers);
                break;
            }

          } catch (Exception e) {
            failCount++;
            errorLog.append("Line ").append(lineNum).append(": ").append(e.getMessage()).append("\n");
          }
        }
      }

      // 3. 처리 완료
      uploadLog.complete(successCount, failCount, errorLog.toString());
      return successCount;

    } catch (IOException e) {
      uploadLog.fail("CSV 파일 읽기 실패: " + e.getMessage());
      throw new RuntimeException("CSV 파일 읽기 실패");
    }
  }

  private void saveStudentDetail(User user, String[] data, Map<String, Integer> headers) {
    String gradeStr = getCsvValue(data, headers, "grade");
    String classNo = getCsvValue(data, headers, "classNo");
    String studentNoStr = getCsvValue(data, headers, "studentNo");

    Integer grade = parseInteger(gradeStr);
    Integer studentNo = parseInteger(studentNoStr);

    StudentDetail studentDetail = StudentDetail.builder()
        .user(user)
        .grade(grade)
        .classNo(classNo)
        .studentNo(studentNo)
        .build();
    studentDetailRepository.save(studentDetail);
  }

  private void saveTeacherDetail(User user, String[] data, Map<String, Integer> headers) {
    String subjectName = getCsvValue(data, headers, "subject");
    String homeroomGradeStr = getCsvValue(data, headers, "homeroomGrade");
    String homeroomClassStr = getCsvValue(data, headers, "homeroomClass");

    Subject subject = null;
    if (subjectName != null && !subjectName.isEmpty()) {
      subject = subjectRepository.findByName(subjectName).orElse(null);
    }

    Integer hrGrade = parseInteger(homeroomGradeStr);
    Integer hrClass = parseInteger(homeroomClassStr);

    TeacherDetail teacherDetail = TeacherDetail.builder()
        .user(user)
        .subject(subject)
        .homeroomGrade(hrGrade)
        .homeroomClassNo(hrClass)
        .build();
    teacherDetailRepository.save(teacherDetail);
  }

  private void saveAdminDetail(User user, String[] data, Map<String, Integer> headers) {
    String levelStr = getCsvValue(data, headers, "adminLevel");

    AdminLevel level = AdminLevel.LEVEL_5; // 기본값
    if ("1".equals(levelStr)) {
      level = AdminLevel.LEVEL_1;
    }

    AdminDetail adminDetail = AdminDetail.builder()
        .user(user)
        .level(level)
        .build();
    adminDetailRepository.save(adminDetail);
  }

  private Integer parseInteger(String value) {
    if (value == null || value.isEmpty())
      return null;
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Map<String, Integer> parseHeaders(String headerLine) {
    Map<String, Integer> map = new HashMap<>();
    String[] cols = headerLine.split(",");
    for (int i = 0; i < cols.length; i++) {
      map.put(cols[i].trim().toLowerCase(), i);
    }
    return map;
  }

  private String getCsvValue(String[] data, Map<String, Integer> headers, String columnName) {
    String key = columnName.toLowerCase();
    if (!headers.containsKey(key))
      return null;

    int index = headers.get(key);
    if (index >= data.length)
      return null;

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
          String birthDateStr = getCsvValue(data, headers, "birthDate");

          Role role = null;
          if (roleStr != null) {
            try {
              String roleInput = roleStr.toUpperCase().replace("ROLE_", "");
              role = Role.valueOf(roleInput);
            } catch (IllegalArgumentException e) {
              // ignore
            }
          }

          LocalDate birthDate = null;
          if (birthDateStr != null && !birthDateStr.isEmpty()) {
            try {
              birthDate = LocalDate.parse(birthDateStr);
            } catch (Exception e) {
              // ignore
            }
          }

          user.updateBatchInfo(name, role, birthDate);
          count++;
        }
      }
      return count;
    } catch (IOException e) {
      throw new RuntimeException("CSV 처리 실패", e);
    }
  }

  @Transactional
  public void internalActivate(String email, String encryptedPassword) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    user.activate(encryptedPassword);
    userRepository.save(user);
  }
}
