package com.mycompany.project.user.command.application.service;

import com.mycompany.project.common.enums.Role;
import com.mycompany.project.common.enums.UserStatus;

import com.mycompany.project.common.aop.SystemLoggable;
import com.mycompany.project.common.entity.BulkUploadLog;
import com.mycompany.project.common.entity.ChangeType;
import com.mycompany.project.common.entity.UploadStatus;
import com.mycompany.project.common.entity.UploadType;
import com.mycompany.project.common.repository.BulkUploadLogRepository;
import com.mycompany.project.user.command.application.dto.*;
import com.mycompany.project.user.command.domain.aggregate.*;
import com.mycompany.project.user.command.domain.repository.*;
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
  private final AdminDetailRepository adminDetailRepository;
  private final BulkUploadLogRepository bulkUploadLogRepository;

  @Transactional
  @SystemLoggable(type = ChangeType.CREATE, tableCodeId = 1)
  public Long registerUser(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email already in use.");
    }

    UserStatus initialStatus = UserStatus.INACTIVE;

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

    userRepository.save(user);
    createRoleDetailFromRequest(user, request);

    return user.getUserId();
  }

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
      builder.subjectId(detailReq.getSubjectId())
          .homeroomGrade(detailReq.getHomeroomGrade())
          .homeroomClassNo(detailReq.getHomeroomClass());
    }
    teacherDetailRepository.save(builder.build());
  }

  private void createAdminDetailFromRequest(User user, AdminDetailRequest detailReq) {
    AdminLevel level = AdminLevel.LEVEL_5;
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
    BulkUploadLog uploadLog = BulkUploadLog.builder()
        .uploadType(UploadType.USER_REG)
        .status(UploadStatus.PENDING)
        .build();
    bulkUploadLogRepository.save(uploadLog);
    uploadLog.startProcessing();

    int successCount = 0;
    int failCount = 0;
    StringBuilder errorLog = new StringBuilder();

    try {
      String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
      try (BufferedReader br = new BufferedReader(new StringReader(csvContent))) {
        String headerLine = br.readLine();
        if (headerLine == null) {
          uploadLog.complete(0, 0, "Empty file");
          return 0;
        }

        Map<String, Integer> headers = parseHeaders(headerLine);
        if (!(headers.containsKey("email") && headers.containsKey("password"))) {
          uploadLog.fail("Required columns (email, password) are missing.");
          throw new IllegalArgumentException("Required columns (email, password) are missing.");
        }

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
              errorLog.append("Line ").append(lineNum).append(": Insufficient data\n");
              continue;
            }

            String email = getCsvValue(data, headers, "email");
            if (email == null || userRepository.existsByEmail(email)) {
              failCount++;
              errorLog.append("Line ").append(lineNum).append(": Email duplicate or missing\n");
              continue;
            }

            String name = getCsvValue(data, headers, "name");
            String password = getCsvValue(data, headers, "password");
            String roleStr = getCsvValue(data, headers, "role");
            String birthDateStr = getCsvValue(data, headers, "birthDate");

            Role role;
            try {
              String roleInput = roleStr.toUpperCase().replace("ROLE_", "");
              role = Role.valueOf(roleInput);
            } catch (IllegalArgumentException | NullPointerException e) {
              role = Role.STUDENT;
            }

            LocalDate birthDate = null;
            String authCode = null;
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
              try {
                birthDate = LocalDate.parse(birthDateStr);
                authCode = birthDate.format(DateTimeFormatter.ofPattern("yyMMdd"));
              } catch (Exception e) {
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
      uploadLog.complete(successCount, failCount, errorLog.toString());
      return successCount;

    } catch (IOException e) {
      uploadLog.fail("CSV file read failed: " + e.getMessage());
      throw new RuntimeException("CSV file read failed");
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
    String subjectIdStr = getCsvValue(data, headers, "subjectid");
    Integer hrGrade = parseInteger(getCsvValue(data, headers, "homeroomgrade"));
    Integer hrClass = parseInteger(getCsvValue(data, headers, "homeroomclass"));
    Long subjectId = subjectIdStr != null ? Long.parseLong(subjectIdStr) : null;

    TeacherDetail teacherDetail = TeacherDetail.builder()
        .user(user)
        .subjectId(subjectId)
        .homeroomGrade(hrGrade)
        .homeroomClassNo(hrClass)
        .build();
    teacherDetailRepository.save(teacherDetail);
  }

  private void saveAdminDetail(User user, String[] data, Map<String, Integer> headers) {
    String levelStr = getCsvValue(data, headers, "adminlevel");
    AdminLevel level = AdminLevel.LEVEL_5;
    if ("1".equals(levelStr))
      level = AdminLevel.LEVEL_1;
    AdminDetail adminDetail = AdminDetail.builder().user(user).level(level).build();
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
        if (!headers.containsKey("email"))
          throw new IllegalArgumentException("Key (email) column is required.");

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
          String birthDateStr = getCsvValue(data, headers, "birthdate");

          Role role = null;
          if (roleStr != null) {
            try {
              String roleInput = roleStr.toUpperCase().replace("ROLE_", "");
              role = Role.valueOf(roleInput);
            } catch (IllegalArgumentException e) {
            }
          }

          LocalDate birthDate = null;
          if (birthDateStr != null && !birthDateStr.isEmpty()) {
            try {
              birthDate = LocalDate.parse(birthDateStr);
            } catch (Exception e) {
            }
          }
          user.updateBatchInfo(name, role, birthDate);
          count++;
        }
      }
      return count;
    } catch (IOException e) {
      throw new RuntimeException("CSV processing failed", e);
    }
  }

  @Transactional
  public void internalActivate(String email, String encryptedPassword) {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    user.activate(encryptedPassword);
    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public User findById(Long userId) {
    return userRepository.findById(java.util.Objects.requireNonNull(userId)).orElse(null);
  }

  @Transactional(readOnly = true)
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }

  @Transactional
  public void recordLoginSuccess(String email) {
    userRepository.findByEmail(email).ifPresent(User::loginSuccess);
  }

  @Transactional
  public void recordLoginFail(String email) {
    userRepository.findByEmail(email).ifPresent(User::loginFail);
  }
}
