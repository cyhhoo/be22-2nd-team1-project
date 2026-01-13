package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseServiceCreationTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("강좌 개설 신청 성공 테스트 (초기 상태: PENDING)")
    void createCourseTest() {
        // 1. 요청 DTO 생성
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setName("New Course");
        dto.setCourseType(CourseType.ELECTIVE);
        dto.setMaxCapacity(30);
        dto.setTuition(150000);
        dto.setAcademicYearId(1L);
        dto.setTeacherDetailId(200L);
        dto.setSubjectId(50L);

        // 시간표 추가
        List<com.mycompany.project.course.dto.TimeSlotDTO> timeSlots = new ArrayList<>();
        com.mycompany.project.course.dto.TimeSlotDTO slot = new com.mycompany.project.course.dto.TimeSlotDTO();
        slot.setDayOfWeek("MON");
        slot.setPeriod(1);
        slot.setClassroom("Room 101");
        timeSlots.add(slot);
        dto.setTimeSlots(timeSlots);

        // 2. 서비스 호출
        courseService.createCourse(dto);

        // 3. 검증 (PENDING 상태 확인)
        List<Course> allCourses = courseRepository.findAll();
        assertFalse(allCourses.isEmpty());
        Course createdCourse = allCourses.get(allCourses.size() - 1); // 가장 최근

        assertEquals("New Course", createdCourse.getName());
        assertEquals(CourseStatus.PENDING, createdCourse.getStatus());
        assertEquals(1, createdCourse.getTimeSlots().size());
    }

    @Test
    @Transactional
    @DisplayName("강좌 개설 실패: 교사 스케줄 중복")
    void duplicateTeacherTest() {
        // 1. 기존 강좌 등록 (선생님 200번, MON 1교시)
        CourseCreateReqDTO dto1 = new CourseCreateReqDTO();
        dto1.setName("Course 1");
        dto1.setCourseType(CourseType.MANDATORY);
        dto1.setMaxCapacity(30);
        dto1.setTuition(100000); // 필수값
        dto1.setSubjectId(10L); // 필수값
        dto1.setAcademicYearId(1L);
        dto1.setTeacherDetailId(200L); // 선생님 200번

        com.mycompany.project.course.dto.TimeSlotDTO slot1 = new com.mycompany.project.course.dto.TimeSlotDTO();
        slot1.setDayOfWeek("MON");
        slot1.setPeriod(1);
        slot1.setClassroom("Room 101");
        dto1.setTimeSlots(Collections.singletonList(slot1));

        courseService.createCourse(dto1);
        entityManager.flush();
        entityManager.clear();
        System.out.println("Step 1 Complete: DTO1 saved and flushed.");

        // 2. 중복 강좌 신청 (선생님 200번, MON 1교시, 다른 강의실)
        CourseCreateReqDTO dto2 = new CourseCreateReqDTO();
        dto2.setName("Course 2");
        dto2.setCourseType(CourseType.MANDATORY);
        dto2.setMaxCapacity(30);
        dto2.setTuition(100000); // 필수값
        dto2.setSubjectId(20L); // 필수값
        dto2.setAcademicYearId(1L);
        dto2.setTeacherDetailId(200L); // 같은 선생님

        com.mycompany.project.course.dto.TimeSlotDTO slot2 = new com.mycompany.project.course.dto.TimeSlotDTO();
        slot2.setDayOfWeek("MON");
        slot2.setPeriod(1);
        slot2.setClassroom("Room 102"); // 강의실은 다름
        dto2.setTimeSlots(Collections.singletonList(slot2));

        // 3. 예외 검증
        System.out.println("Step 2 Starting: Attempting to save conflicting DTO2...");
        assertThrows(IllegalStateException.class, () -> courseService.createCourse(dto2));
        System.out.println("Step 3 Complete: Exception thrown as expected.");
    }

    @Test
    @Transactional
    @DisplayName("강좌 개설 실패: 강의실 스케줄 중복")
    void duplicateClassroomTest() {
        // 1. 기존 강좌 등록 (Room 101, MON 1교시)
        CourseCreateReqDTO dto1 = new CourseCreateReqDTO();
        dto1.setName("Course 1");
        dto1.setCourseType(CourseType.MANDATORY); // 필수값
        dto1.setMaxCapacity(30);
        dto1.setTuition(100000); // 필수값
        dto1.setSubjectId(10L); // 필수값
        dto1.setAcademicYearId(1L);
        dto1.setTeacherDetailId(200L);

        com.mycompany.project.course.dto.TimeSlotDTO slot1 = new com.mycompany.project.course.dto.TimeSlotDTO();
        slot1.setDayOfWeek("MON");
        slot1.setPeriod(1);
        slot1.setClassroom("Room 101");
        dto1.setTimeSlots(Collections.singletonList(slot1));

        courseService.createCourse(dto1);
        entityManager.flush();
        entityManager.clear();

        // 2. 중복 강의실 신청 (다른 선생님, 같은 강의실, 같은 시간)
        CourseCreateReqDTO dto2 = new CourseCreateReqDTO();
        dto2.setName("Course 2");
        dto2.setCourseType(CourseType.ELECTIVE); // 필수값
        dto2.setMaxCapacity(30);
        dto2.setTuition(100000); // 필수값
        dto2.setSubjectId(30L); // 필수값
        dto2.setAcademicYearId(1L);
        dto2.setTeacherDetailId(300L); // 다른 선생님

        com.mycompany.project.course.dto.TimeSlotDTO slot2 = new com.mycompany.project.course.dto.TimeSlotDTO();
        slot2.setDayOfWeek("MON");
        slot2.setPeriod(1);
        slot2.setClassroom("Room 101"); // 같은 강의실
        dto2.setTimeSlots(Collections.singletonList(slot2));

        // 3. 예외 검증
        assertThrows(IllegalStateException.class, () -> courseService.createCourse(dto2));
    }

    @Test
    @DisplayName("강좌 개설 실패: 최대 정원이 0 이하일 경우 예외 발생")
    void invalidCapacityTest() {
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setName("Invalid Capacity Course");
        dto.setMaxCapacity(0); // 0명 (실패 유도)
        dto.setTuition(100000);

        assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(dto));
    }

    @Test
    @Transactional
    @DisplayName("강좌 승인(OPEN) 및 반려(REFUSE) 상태 변경 테스트")
    void approveAndRefuseTest() {
        // 1. 강좌 생성 (PENDING)
        Course course = Course.builder().name("Approve Test").maxCapacity(10).build();
        course = courseRepository.save(course);
        course.update(null, null, null, null, null, null, null, CourseStatus.PENDING);
        Long courseId = course.getId();

        // 2. 승인
        courseService.approveCourse(courseId);
        entityManager.flush();
        entityManager.clear();

        Course approvedCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(CourseStatus.OPEN, approvedCourse.getStatus());

        // 3. 다시 PENDING으로 돌리고 반려 테스트 (Test용)
        approvedCourse.update(null, null, null, null, null, null, null, CourseStatus.PENDING);
        courseRepository.save(approvedCourse);
        entityManager.flush();
        entityManager.clear();

        // 4. 반려
        courseService.refuseCourse(courseId, "Test Refuse Reason");
        entityManager.flush();
        entityManager.clear();

        Course refusedCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(CourseStatus.REFUSE, refusedCourse.getStatus());
    }
}
