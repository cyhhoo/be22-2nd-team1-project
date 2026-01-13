package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseServiceTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("강좌 엔티티 기본 저장 테스트")
    void insertTest() {
        Course newCourse = Course.builder()
                .name("name")
                .academicYearId(1L)
                .maxCapacity(10)
                .teacherDetailId(123L)
                .courseType(CourseType.MANDATORY)
                .build();

        Course course = courseRepository.save(newCourse);
        assertNotNull(course);
    }

    @Test
    @Transactional
    @DisplayName("강좌 정보 수정 성공 테스트")
    void updateTest() {
        // 1. 강좌 생성 및 저장
        Course newCourse = Course.builder()
                .name("Original Name")
                .academicYearId(1L)
                .maxCapacity(10)
                .teacherDetailId(123L)
                .courseType(CourseType.MANDATORY)
                .tuition(100000)
                .build();
        Course savedCourse = courseRepository.save(newCourse);

        // 2. 수정 데이터 준비 (DTO)
        CourseUpdateReqDTO updateReqDTO = new CourseUpdateReqDTO();
        updateReqDTO.setName("Updated Name");
        updateReqDTO.setCourseType(CourseType.ELECTIVE);
        updateReqDTO.setMaxCapacity(20);
        updateReqDTO.setTuition(200000);
        updateReqDTO.setStatus(CourseStatus.CLOSED);

        // 3. 수정 서비스 호출
        courseService.updateCourse(savedCourse.getId(), updateReqDTO);

        // 4. 조회 및 검증
        Course updatedCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();

        assertEquals("Updated Name", updatedCourse.getName());
        assertEquals(CourseType.ELECTIVE, updatedCourse.getCourseType());
        assertEquals(20, updatedCourse.getMaxCapacity());
        assertEquals(200000, updatedCourse.getTuition());
        assertEquals(CourseStatus.CLOSED, updatedCourse.getStatus());
    }

    @Test
    @DisplayName("강좌 정보 수정 실패 - 존재하지 않는 강좌 ID")
    void updateTest_Fail_NotFound() {
        // 1. 존재하지 않는 ID와 DTO 준비
        Long invalidId = 9999L;
        CourseUpdateReqDTO dto = new CourseUpdateReqDTO();
        dto.setName("New Name");

        // 2. 서비스 호출 및 예외 검증
        assertThrows(IllegalArgumentException.class, () -> {
            courseService.updateCourse(invalidId, dto);
        });
    }

}