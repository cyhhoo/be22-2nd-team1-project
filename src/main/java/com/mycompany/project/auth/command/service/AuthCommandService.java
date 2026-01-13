package com.mycompany.project.auth.command.service;

import com.mycompany.project.auth.command.dto.*;
import com.mycompany.project.auth.query.dto.TokenResponse;
import com.mycompany.project.common.aop.SystemLoggable;
import com.mycompany.project.common.entity.ChangeType;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.jwtsecurity.JwtTokenProvider;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.user.command.domain.aggregate.*;
import com.mycompany.project.user.command.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final StudentDetailRepository studentDetailRepository;
    private final TeacherDetailRepository teacherDetailRepository;
    private final AdminDetailRepository adminDetailRepository;
    private final SubjectRepository subjectRepository;

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
        createRoleDetail(user, request);

        return user.getUserId();
    }

    /**
     * 역할에 따라 StudentDetail, TeacherDetail, AdminDetail 생성
     */
    private void createRoleDetail(User user, UserRegisterRequest request) {
        switch (user.getRole()) {
            case STUDENT:
                createStudentDetail(user, request.getStudentDetail());
                break;
            case TEACHER:
                createTeacherDetail(user, request.getTeacherDetail());
                break;
            case ADMIN:
                createAdminDetail(user, request.getAdminDetail());
                break;
        }
    }

    private void createStudentDetail(User user, StudentDetailRequest detailReq) {
        StudentDetail.StudentDetailBuilder builder = StudentDetail.builder().user(user);

        if (detailReq != null) {
            builder.grade(detailReq.getGrade())
                    .classNo(detailReq.getClassNo())
                    .studentNo(detailReq.getStudentNo());
        }

        studentDetailRepository.save(builder.build());
    }

    private void createTeacherDetail(User user, TeacherDetailRequest detailReq) {
        TeacherDetail.TeacherDetailBuilder builder = TeacherDetail.builder().user(user);

        if (detailReq != null) {
            // 과목 조회 (subjectId로 조회)
            if (detailReq.getSubjectId() != null) {
                Subject subject = subjectRepository.findById(detailReq.getSubjectId()).orElse(null);
                builder.subject(subject);
            }
            builder.homeroomGrade(detailReq.getHomeroomGrade())
                    .homeroomClassNo(detailReq.getHomeroomClass());
        }

        teacherDetailRepository.save(builder.build());
    }

    private void createAdminDetail(User user, AdminDetailRequest detailReq) {
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
    public TokenResponse activateAccount(AccountActivationRequest request) {
        // 1. 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 이미 활성화 된 경우
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

        // 활성화 된 상태로 바로 즉시 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getUserId(), user.getEmail(), user.getRole(), user.getStatus());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // DB에 리프레시 토큰 저장
        Token tokenEntity = Token.builder()
                .token(refreshToken)
                .email(user.getEmail())
                .build();
        tokenRepository.save(tokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }
}
