/*
package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.TeacherTimetableResDTO;
import com.mycompany.project.course.mapper.CourseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CourseServiceTimetableTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseMapper courseMapper;

    @Test
    @DisplayName("교사 주간 시간표 조회 - Grid 데이터 변환 검증")
    void getTeacherTimetable_Success() {
        // Given
        Long teacherId = 1L;
        Long academicYearId = 202501L;

        // Mocking Mapper Result (Flat List)
        List<Map<String, Object>> mockResult = List.of(
                Map.of(
                        "dayOfWeek", "MON",
                        "period", 1,
                        "courseId", 10L,
                        "courseName", "Math",
                        "classroom", "A101",
                        "courseType", "MANDATORY"),
                Map.of(
                        "dayOfWeek", "WED",
                        "period", 3,
                        "courseId", 11L,
                        "courseName", "Science",
                        "classroom", "B102",
                        "courseType", "ELECTIVE"));

        given(courseMapper.findTeacherTimetable(academicYearId, teacherId)).willReturn(mockResult);

        // When
        TeacherTimetableResDTO result = courseService.getTeacherTimetable(teacherId, academicYearId);

        // Then
        assertThat(result.getTimeSlots()).hasSize(2);

        // Monday Check
        TeacherTimetableResDTO.TimeSlotInfo slot1 = result.getTimeSlots().stream()
                .filter(s -> s.getDayOfWeek().equals("MON"))
                .findFirst().orElseThrow();
        assertThat(slot1.getPeriod()).isEqualTo(1);
        assertThat(slot1.getCourseName()).isEqualTo("Math");

        // Wednesday Check
        TeacherTimetableResDTO.TimeSlotInfo slot2 = result.getTimeSlots().stream()
                .filter(s -> s.getDayOfWeek().equals("WED"))
                .findFirst().orElseThrow();
        assertThat(slot2.getPeriod()).isEqualTo(3);
        assertThat(slot2.getCourseName()).isEqualTo("Science");
    }
}
*/
