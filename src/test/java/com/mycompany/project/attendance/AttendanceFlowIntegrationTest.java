package com.mycompany.project.attendance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.project.attendance.dto.request.AttendanceClosureRequest;
import com.mycompany.project.attendance.dto.request.AttendanceConfirmRequest;
import com.mycompany.project.attendance.dto.request.AttendanceCreateRequest;
import com.mycompany.project.attendance.dto.request.CorrectionCreateRequest;
import com.mycompany.project.attendance.dto.request.CorrectionDecideRequest;
import com.mycompany.project.attendance.entity.Attendance;
import com.mycompany.project.attendance.entity.AttendanceCode;
import com.mycompany.project.attendance.entity.AttendanceCorrectionRequest;
import com.mycompany.project.attendance.entity.enums.AttendanceState;
import com.mycompany.project.attendance.entity.enums.CorrectionStatus;
import com.mycompany.project.attendance.entity.enums.ScopeType;
import com.mycompany.project.attendance.repository.AttendanceClosureRepository;
import com.mycompany.project.attendance.repository.AttendanceCodeRepository;
import com.mycompany.project.attendance.repository.AttendanceCorrectionRequestRepository;
import com.mycompany.project.attendance.repository.AttendanceRepository;
import com.mycompany.project.attendance.service.AttendanceCommandService;
import com.mycompany.project.attendance.service.AttendanceCorrectionCommandService;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicYear;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.schedule.command.domain.repository.AcademicYearRepository;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.aggregate.TeacherDetail;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.repository.SubjectRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.hasItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AttendanceFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AttendanceCodeRepository attendanceCodeRepository;

    @Autowired
    private AttendanceCorrectionRequestRepository correctionRequestRepository;

    @Autowired
    private AttendanceClosureRepository attendanceClosureRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherDetailRepository teacherDetailRepository;

    @Autowired
    private StudentDetailRepository studentDetailRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private AttendanceCommandService attendanceCommandService;

    @Autowired
    private AttendanceCorrectionCommandService attendanceCorrectionCommandService;

    private User teacher;
    private User admin;
    private User student;
    private TeacherDetail teacherDetail;
    private StudentDetail studentDetail;
    private Subject subject;
    private Course course;
    private Enrollment enrollment;
    private AttendanceCode presentCode;
    private AttendanceCode lateCode;
    private AcademicYear academicYear;

    @BeforeEach
    void setUp() {
        correctionRequestRepository.deleteAll();
        attendanceClosureRepository.deleteAll();
        attendanceRepository.deleteAll();
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        teacherDetailRepository.deleteAll();
        studentDetailRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
        attendanceCodeRepository.deleteAll();
        academicYearRepository.deleteAll();

        teacher = userRepository.save(User.builder()
            .email("teacher@example.com")
            .password("test-password")
            .name("Teacher")
            .role(Role.TEACHER)
            .status(UserStatus.ACTIVE)
            .birthDate("1990-01-01")
            .build());

        admin = userRepository.save(User.builder()
            .email("admin@example.com")
            .password("test-password")
            .name("Admin")
            .role(Role.ADMIN)
            .status(UserStatus.ACTIVE)
            .birthDate("1985-01-01")
            .build());

        student = userRepository.save(User.builder()
            .email("student@example.com")
            .password("test-password")
            .name("Student")
            .role(Role.STUDENT)
            .status(UserStatus.ACTIVE)
            .birthDate("2007-01-01")
            .build());

        subject = subjectRepository.save(Subject.builder()
            .name("Math")
            .build());

        teacherDetail = teacherDetailRepository.save(TeacherDetail.builder()
            .user(teacher)
            .subject(subject)
            .homeroomGrade(1)
            .homeroomClassNo(1)
            .build());

        studentDetail = studentDetailRepository.save(StudentDetail.builder()
            .user(student)
            .grade(1)
            .classNo("1")
            .studentNo(1)
            .build());

        academicYear = academicYearRepository.save(AcademicYear.builder()
            .year(2025)
            .semester(1)
            .startDate(LocalDate.of(2025, 3, 1))
            .endDate(LocalDate.of(2025, 7, 31))
            .isCurrent(true)
            .build());

        course = courseRepository.save(Course.builder()
            .teacherDetail(teacherDetail)
            .academicYearId(academicYear.getAcademicYearId())
            .subjectId(subject.getId())
            .name("Math")
            .courseType(CourseType.MANDATORY)
            .maxCapacity(30)
            .tuition(0)
            .build());

        enrollment = enrollmentRepository.save(Enrollment.builder()
            .studentDetail(studentDetail)
            .course(course)
            .build());

        presentCode = attendanceCodeRepository.save(new AttendanceCode("PRESENT", "Present", false));
        lateCode = attendanceCodeRepository.save(new AttendanceCode("LATE", "Late", false));
    }

    @Test
    @DisplayName("Attendance flow should generate, confirm, close, and correct")
    void attendanceFlow() throws Exception {
        LocalDate classDate = LocalDate.of(2025, 3, 1);

        AttendanceCreateRequest generateRequest = AttendanceCreateRequest.builder()
            .courseId(course.getCourseId())
            .classDate(classDate)
            .period(1)
            .userId(teacher.getUserId())
            .build();

        mockMvc.perform(post("/api/v1/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].attendanceCodeName").value("Present"));

        mockMvc.perform(get("/api/v1/attendance/codes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[*].code", hasItem("PRESENT")));

        AttendanceConfirmRequest confirmRequest = AttendanceConfirmRequest.builder()
            .courseId(course.getCourseId())
            .classDate(classDate)
            .period(1)
            .userId(teacher.getUserId())
            .build();

        mockMvc.perform(post("/api/v1/attendance/confirmations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmRequest)))
            .andExpect(status().isOk());

        Attendance attendance = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
            enrollment.getEnrollmentId(), classDate, (byte) 1
        ).orElseThrow();
        assertThat(attendance.getState()).isEqualTo(AttendanceState.CONFIRMED);

        AttendanceClosureRequest closureRequest = AttendanceClosureRequest.builder()
            .academicYearId(academicYear.getAcademicYearId())
            .scopeType(ScopeType.MONTH)
            .scopeValue("2025-03")
            .courseId(course.getCourseId())
            .userId(admin.getUserId())
            .build();

        mockMvc.perform(post("/api/v1/attendance/closures")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(closureRequest)))
            .andExpect(status().isOk());

        Attendance closedAttendance = attendanceRepository.findById(attendance.getId()).orElseThrow();
        assertThat(closedAttendance.getState()).isEqualTo(AttendanceState.CLOSED);
        assertThat(attendanceClosureRepository.count()).isEqualTo(1);

        CorrectionCreateRequest correctionCreateRequest = CorrectionCreateRequest.builder()
            .attendanceId(attendance.getId())
            .requestedAttendanceCodeId(lateCode.getId())
            .requestReason("Correction requested")
            .requestedBy(teacher.getUserId())
            .build();

        mockMvc.perform(post("/api/v1/attendance/corrections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correctionCreateRequest)))
            .andExpect(status().isOk());

        AttendanceCorrectionRequest correctionRequest = correctionRequestRepository.findAll().stream()
            .findFirst()
            .orElseThrow();

        CorrectionDecideRequest decideRequest = CorrectionDecideRequest.builder()
            .requestId(correctionRequest.getId())
            .approved(true)
            .adminId(admin.getUserId())
            .build();

        mockMvc.perform(patch("/api/v1/attendance/corrections/{requestId}", correctionRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decideRequest)))
            .andExpect(status().isOk());

        Attendance updatedAttendance = attendanceRepository.findById(attendance.getId()).orElseThrow();
        AttendanceCorrectionRequest decidedRequest = correctionRequestRepository.findById(correctionRequest.getId()).orElseThrow();

        assertThat(updatedAttendance.getAttendanceCodeId()).isEqualTo(lateCode.getId());
        assertThat(decidedRequest.getStatus()).isEqualTo(CorrectionStatus.APPROVED);
    }


    @Test
    @DisplayName("권한: 학생은 출석부 생성(generate)을 할 수 없다")
    void studentCannotGenerateAttendances() {
        // 학생이 출석부를 생성하려고 시도하는 상황
        AttendanceCreateRequest request = AttendanceCreateRequest.builder()
                .courseId(course.getCourseId())
                .classDate(LocalDate.of(2025, 3, 1))
                .period(1)
                .userId(student.getUserId()) // ❗ 학생 ID로 요청
                .build();

        // 담당 교사만 가능하므로 예외가 발생해야 정상
        assertThatThrownBy(() -> attendanceCommandService.generateAttendances(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("상태 전이: SAVED 상태 출결은 정정요청 생성이 막혀야 한다(확정/마감만 가능)")
    void cannotCreateCorrectionRequestWhenAttendanceIsSaved() {
        LocalDate classDate = LocalDate.of(2025, 3, 1);

        // 1) 담당 교사가 출석부를 생성한다.
        //    - 생성된 출결은 기본적으로 SAVED 상태로 생성됨(너가 만든 Attendance 엔티티 생성자 로직 기준)
        AttendanceCreateRequest generateRequest = AttendanceCreateRequest.builder()
                .courseId(course.getCourseId())
                .classDate(classDate)
                .period(1)
                .userId(teacher.getUserId())
                .build();
        attendanceCommandService.generateAttendances(generateRequest);

        Attendance savedAttendance = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
                enrollment.getEnrollmentId(), classDate, (byte) 1
        ).orElseThrow();

        // 2) SAVED 상태인 출결에 대해 정정요청 생성 시도
        CorrectionCreateRequest correctionCreateRequest = CorrectionCreateRequest.builder()
                .attendanceId(savedAttendance.getId())
                .requestedAttendanceCodeId(lateCode.getId())
                .requestReason("SAVED 상태에서 정정요청 시도")
                .requestedBy(teacher.getUserId()) // 교사 본인
                .build();

        // 3) 정책: "확정(CONFIRMED) 또는 마감(CLOSED)된 출결만 정정요청 가능"
        //    => SAVED면 예외가 떠야 정상
        assertThatThrownBy(() -> attendanceCorrectionCommandService.createCorrectionRequest(correctionCreateRequest))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("중복 방지: 같은 날짜/교시에 generate를 두 번 호출해도 출결이 중복 생성되면 안 된다")
    void generateAttendancesShouldNotCreateDuplicates() {
        LocalDate classDate = LocalDate.of(2025, 3, 1);

        AttendanceCreateRequest request = AttendanceCreateRequest.builder()
                .courseId(course.getCourseId())
                .classDate(classDate)
                .period(1)
                .userId(teacher.getUserId())
                .build();

        // 1) 첫 번째 generate: 출결 생성
        attendanceCommandService.generateAttendances(request);

        // 2) 두 번째 generate: 이미 있으면 "생성 안 하고" 조회만 반환하는 게 목표
        attendanceCommandService.generateAttendances(request);

        // 현재 테스트 셋업에서는 enrollment가 1건이므로 attendance도 1건이어야 정상
        long attendanceCount = attendanceRepository.count();
        assertThat(attendanceCount).isEqualTo(1);

        // 혹시 불안하면, 실제로 같은 키(enrollmentId + date + period)로 1건만 존재하는지도 확인 가능
        Attendance one = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
                enrollment.getEnrollmentId(), classDate, (byte) 1
        ).orElseThrow();
        assertThat(one).isNotNull();
    }
}
