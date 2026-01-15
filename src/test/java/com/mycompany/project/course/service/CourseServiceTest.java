package com.mycompany.project.course.service;

import com.mycompany.project.attendance.repository.AttendanceRepository;
import com.mycompany.project.common.service.RefundService;
import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.dto.TimeSlotDTO;
import com.mycompany.project.course.entity.*;
import com.mycompany.project.course.mapper.CourseMapper;
import com.mycompany.project.course.repository.CourseChangeRequestRepository;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.course.repository.CourseTimeSlotRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.aggregate.TeacherDetail;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private CourseChangeRequestRepository courseChangeRequestRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private RefundService refundService;
    @Mock
    private StudentDetailRepository studentDetailRepository;
    @Mock
    private TeacherDetailRepository teacherDetailRepository;

    @Test
    @DisplayName("강좌 개설 성공")
    void createCourse_Success() {
        // given
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setName("Java Programming");
        dto.setCourseType(CourseType.MANDATORY);
        dto.setMaxCapacity(30);
        dto.setTuition(100000);
        dto.setSubjectId(1L);
        dto.setAcademicYearId(1L);
        dto.setTeacherDetailId(100L);

        List<TimeSlotDTO> timeSlots = new ArrayList<>();
        TimeSlotDTO slot1 = new TimeSlotDTO();
        slot1.setDayOfWeek("MONDAY");
        slot1.setPeriod(1);
        slot1.setClassroom("101");
        timeSlots.add(slot1);
        dto.setTimeSlots(timeSlots);

        TeacherDetail teacher = mock(TeacherDetail.class);
        given(teacherDetailRepository.getReferenceById(100L)).willReturn(teacher);

        Course savedCourse = Course.builder().courseId(1L).status(CourseStatus.PENDING).build();
        given(courseRepository.save(any(Course.class))).willReturn(savedCourse); // 첫번째 save

        // Conflict checks
        given(courseMapper.countTeacherSchedule(anyLong(), anyLong(), anyString(), anyInt())).willReturn(0);
        given(courseMapper.countClassroomSchedule(anyLong(), anyString(), anyString(), anyInt())).willReturn(0);

        // when
        courseService.createCourse(dto);

        // then
        verify(courseRepository, times(2)).save(any(Course.class)); // 초기 저장 + 시간표 추가 후 저장
        verify(courseMapper, times(1)).countTeacherSchedule(eq(1L), eq(100L), eq("MONDAY"), eq(1));
    }

    @Test
    @DisplayName("강좌 개설 실패 - 정원 0명 이하")
    void createCourse_Fail_InvalidCapacity() {
        // given
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setMaxCapacity(0);

        // when & then
        assertThatThrownBy(() -> courseService.createCourse(dto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COURSE_BAD_REQUEST);
    }

    @Test
    @DisplayName("강좌 개설 실패 - 교사 시간표 중복")
    void createCourse_Fail_TeacherConflict() {
        // given
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setMaxCapacity(30);
        dto.setTeacherDetailId(100L);
        dto.setAcademicYearId(1L);

        List<TimeSlotDTO> timeSlots = new ArrayList<>();
        TimeSlotDTO slot1 = new TimeSlotDTO();
        slot1.setDayOfWeek("MONDAY");
        slot1.setPeriod(1);
        timeSlots.add(slot1);
        dto.setTimeSlots(timeSlots);

        TeacherDetail teacher = mock(TeacherDetail.class);
        given(teacherDetailRepository.getReferenceById(100L)).willReturn(teacher);
        given(courseRepository.save(any(Course.class))).willReturn(mock(Course.class));

        given(courseMapper.countTeacherSchedule(anyLong(), anyLong(), anyString(), anyInt())).willReturn(1);

        // when & then
        assertThatThrownBy(() -> courseService.createCourse(dto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSTRUCTOR_TIMETABLE_CONFLICT);
    }

    @Test
    @DisplayName("강좌 승인 성공")
    void approveCourse_Success() {
        // given
        Course course = spy(Course.builder().status(CourseStatus.PENDING).build());
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        // when
        courseService.approveCourse(1L);

        // then
        verify(course).update(any(), any(), any(), any(), any(), any(), any(), eq(CourseStatus.OPEN));
    }

    @Test
    @DisplayName("강좌 승인 실패 - 이미 처리된 강좌")
    void approveCourse_Fail_NotPending() {
        // given
        Course course = Course.builder().status(CourseStatus.OPEN).build();
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        // when & then
        assertThatThrownBy(() -> courseService.approveCourse(1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COURSE_CONDITION_MISMATCH);
    }

    @Test
    @DisplayName("강좌 반려 성공")
    void refuseCourse_Success() {
        // given
        Course course = spy(Course.builder().status(CourseStatus.PENDING).build());
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        // when
        courseService.refuseCourse(1L, "Content mismatch");

        // then
        verify(course).setRejectionReason("Content mismatch");
        verify(course).update(any(), any(), any(), any(), any(), any(), any(), eq(CourseStatus.REFUSE));
    }

    @Test
    @DisplayName("강좌 수정 성공")
    void updateCourse_Success() {
        // given
        Course course = spy(Course.builder().status(CourseStatus.OPEN).build());
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        CourseUpdateReqDTO dto = new CourseUpdateReqDTO();
        dto.setName("New Name");
        dto.setStatus(CourseStatus.OPEN);

        // when
        courseService.updateCourse(1L, dto);

        // then
        verify(course).update(eq("New Name"), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("강좌 변경 요청 생성 성공")
    void requestCourseUpdate_Success() {
        // given
        Course course = Course.builder().courseId(1L).build();
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        CourseUpdateReqDTO dto = new CourseUpdateReqDTO();
        dto.setMaxCapacity(50);

        CourseChangeRequest request = CourseChangeRequest.builder().build();
        ReflectionTestUtils.setField(request, "id", 10L);
        given(courseChangeRequestRepository.save(any(CourseChangeRequest.class))).willReturn(request);

        // when
        Long requestId = courseService.requestCourseUpdate(1L, dto, "Need more capacity");

        // then
        assertThat(requestId).isEqualTo(10L);
    }

    @Test
    @DisplayName("강좌 변경 요청 승인 성공")
    void approveChangeRequest_Success() {
        // given
        Course course = spy(Course.builder().courseId(1L).maxCapacity(30).build());
        CourseChangeRequest request = spy(CourseChangeRequest.builder()
                .course(course)
                .targetMaxCapacity(50)
                .build());
        ReflectionTestUtils.setField(request, "id", 10L);

        given(courseChangeRequestRepository.findById(10L)).willReturn(Optional.of(request));

        // when
        courseService.approveChangeRequest(10L);

        // then
        verify(course).updateCourseInfo(any(), any(), eq(50), any(), any(), any(), any());
        verify(request).approve();
    }

    @Test
    @DisplayName("담당 교사 변경 성공")
    void changeTeacher_Success() {
        // given
        Course course = spy(Course.builder().courseId(1L).academicYearId(1L).build());
        // Mocking time slots
        CourseTimeSlot slot = CourseTimeSlot.builder().dayOfWeek("MONDAY").period(1).build();
        List<CourseTimeSlot> slots = new ArrayList<>();
        slots.add(slot);

        // Use doReturn for spying on methods if needed, but here we can just ensure
        // getTimeslots returns our list.
        // Course.getTimeSlots() is a simple getter, but since Course is using @Builder,
        // internal fields might need setting.
        // Since we are mocking dependencies mostly, we can assume course object is real
        // or spy.
        // Reflection or setter might be needed if @Builder default list is null, but
        // here we can rely on proper test object construction.
        // Let's use flexible mocking for course if needed, but real object is better.
        // Problem: Course.getTimeSlots() relies on JPA list. Let's mock the list in the
        // course object if possible or just inject it.
        // Since we can't easily set the list on the object without reflection or
        // helper, let's spy the list getter?
        // Or better, just set it via a helper method if available, or assume
        // implementation.
        // Course class likely has an ArrayList init.
        // Let's rely on `course.addTimeSlot` not being available in existing builder
        // unless we use `toBuilder`.
        // Let's just create a course and add slots if possible.
        // Wait, Course.java definition shows `addTimeSlot`.
        Course realCourse = Course.builder().courseId(1L).academicYearId(1L).build();
        realCourse.addTimeSlot(slot);

        Course spyCourse = spy(realCourse);

        given(courseRepository.findById(1L)).willReturn(Optional.of(spyCourse));
        given(courseMapper.countTeacherSchedule(eq(1L), eq(200L), eq("MONDAY"), eq(1))).willReturn(0);
        given(teacherDetailRepository.getReferenceById(200L)).willReturn(mock(TeacherDetail.class));

        // when
        courseService.changeTeacher(1L, 200L);

        // then
        verify(spyCourse).updateCourseInfo(any(), any(), any(), any(), any(), any(), any());
        verify(notificationService).send(anyList(), anyString());
    }

    @Test
    @DisplayName("학생 수강 일괄 등록 성공")
    void enrollStudents_Success() {
        // given
        Course course = Course.builder().courseId(1L).maxCapacity(30).academicYearId(1L).build();
        // Add a slot
        course.addTimeSlot(CourseTimeSlot.builder().dayOfWeek("MON").period(1).build());

        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        List<Long> studentIds = List.of(10L, 11L);

        User user1 = User.builder().userId(10L).build();
        User user2 = User.builder().userId(11L).build();
        StudentDetail student1 = StudentDetail.builder().id(100L).user(user1).build();
        StudentDetail student2 = StudentDetail.builder().id(101L).user(user2).build();

        given(studentDetailRepository.findAllById(studentIds)).willReturn(List.of(student1, student2));

        // Mock conflict checks (no conflicts)
        given(courseMapper.findConflictingEnrollments(anyLong(), anyLong(), anyString(), anyInt()))
                .willReturn(new ArrayList<>()); // Empty list

        // when
        courseService.enrollStudents(1L, studentIds, false);

        // then
        verify(enrollmentRepository, times(2)).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("학생 수강 등록 실패 - 필수 과목 중복")
    void enrollStudents_Fail_MandatoryConflict() {
        // given
        Course course = Course.builder().courseId(1L).academicYearId(1L).maxCapacity(30).build();
        course.addTimeSlot(CourseTimeSlot.builder().dayOfWeek("MON").period(1).build());
        given(courseRepository.findById(1L)).willReturn(Optional.of(course));

        List<Long> studentIds = List.of(10L);
        StudentDetail student = StudentDetail.builder().id(100L).user(User.builder().userId(10L).build()).build();
        given(studentDetailRepository.findAllById(studentIds)).willReturn(List.of(student));

        // Conflict mock
        Map<String, Object> conflict = Map.of("courseType", "MANDATORY", "enrollmentId", 555L);
        given(courseMapper.findConflictingEnrollments(anyLong(), anyLong(), anyString(), anyInt()))
                .willReturn(List.of(conflict));

        // when & then
        assertThatThrownBy(() -> courseService.enrollStudents(1L, studentIds, false))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDENT_REQUIRED_COURSE_CONFLICT);
    }

    @Test
    @DisplayName("강좌 폐강 성공")
    void deleteCourse_Success() {
        // given
        Long courseId = 1L;
        Course course = spy(Course.builder().courseId(courseId).name("Math").status(CourseStatus.OPEN).build());
        given(courseRepository.findById(courseId)).willReturn(Optional.of(course));

        // Mock enrollments
        Enrollment enrollment = mock(Enrollment.class);
        StudentDetail student = mock(StudentDetail.class);
        User user = mock(User.class);
        given(student.getUser()).willReturn(user);
        given(user.getUserId()).willReturn(10L);
        given(enrollment.getStudentDetail()).willReturn(student);
        given(enrollment.getStatus()).willReturn(EnrollmentStatus.APPLIED);

        // We need to mock getEnrollmentsByCourseId internal call.
        // Since it's private and calls enrollmentRepository.findAll(), let's mock
        // findAll
        // But findAll returns potentially huge list.
        // courseService.getEnrollmentsByCourseId filters by courseId.

        // Let's ensure the mocked enrollment has the correct course
        Course linkedCourse = Course.builder().courseId(courseId).build();
        given(enrollment.getCourse()).willReturn(linkedCourse);

        given(enrollmentRepository.findAll()).willReturn(List.of(enrollment));

        // when
        courseService.deleteCourse(courseId, "Not enough students");

        // then
        verify(course).changeStatus(CourseStatus.CANCELED);
        verify(enrollment).forceCancel(anyString());
        verify(refundService).processRefund(eq(10L), eq(courseId), anyString());
        verify(notificationService).send(anyList(), anyString());
    }

    @Test
    @DisplayName("학생 조회 성공")
    void getStudentDetail_Success() {
        // given
        Long courseId = 1L;
        Long studentId = 10L;

        Enrollment enrollment = mock(Enrollment.class);
        StudentDetail student = mock(StudentDetail.class);
        User user = mock(User.class);
        Course courseLink = Course.builder().courseId(courseId).build();

        given(enrollment.getCourse()).willReturn(courseLink);
        given(enrollment.getStudentDetail()).willReturn(student);
        given(student.getUser()).willReturn(user);
        given(user.getUserId()).willReturn(studentId);
        given(user.getName()).willReturn("John Doe");
        given(enrollment.getMemo()).willReturn("Good student");

        given(enrollmentRepository.findAll()).willReturn(List.of(enrollment));

        // when
        var result = courseService.getStudentDetail(courseId, studentId);

        // then
        assertThat(result.getStudentName()).isEqualTo("John Doe");
        assertThat(result.getMemo()).isEqualTo("Good student");
    }

    @Test
    @DisplayName("전체 강좌 목록 조회")
    void getAllCourses_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Course course = Course.builder().courseId(1L).name("Math").status(CourseStatus.OPEN)
                .courseType(CourseType.MANDATORY)
                .build();
        Page<Course> coursePage = new PageImpl<>(List.of(course));

        given(courseRepository.findAll(pageable)).willReturn(coursePage);

        // when
        Page<com.mycompany.project.course.dto.CourseListResDTO> result = courseService.getAllCourses(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Math");
    }
}
