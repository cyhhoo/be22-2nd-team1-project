package com.mycompany.project.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("강좌 개설 신청 테스트")
    void createCourseTest() throws Exception {
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setName("New Course");
        dto.setCourseType(CourseType.MANDATORY);
        dto.setMaxCapacity(30);
        dto.setTuition(10000);
        dto.setSubjectId(1L);
        dto.setAcademicYearId(1L);
        dto.setTeacherDetailId(1L);

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(courseService).createCourse(any(CourseCreateReqDTO.class));
    }

    @Test
    @DisplayName("강좌 승인 테스트")
    void approveCourseTest() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(post("/api/courses/{courseId}/approve", courseId))
                .andExpect(status().isOk());

        verify(courseService).approveCourse(courseId);
    }

    @Test
    @DisplayName("강좌 반려 테스트")
    void refuseCourseTest() throws Exception {
        Long courseId = 1L;
        String reason = "Not enough capacity plan";

        mockMvc.perform(post("/api/courses/{courseId}/refuse", courseId)
                .param("reason", reason))
                .andExpect(status().isOk());

        verify(courseService).refuseCourse(courseId, reason);
    }

    @Test
    @DisplayName("강좌 변경 요청 테스트")
    void requestCourseUpdateTest() throws Exception {
        Long courseId = 1L;
        String reason = "Teacher Change";
        CourseUpdateReqDTO dto = new CourseUpdateReqDTO();
        dto.setMaxCapacity(40);

        given(courseService.requestCourseUpdate(anyLong(), any(CourseUpdateReqDTO.class), anyString()))
                .willReturn(100L);

        mockMvc.perform(post("/api/courses/{courseId}/request-update", courseId)
                .param("reason", reason)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(courseService).requestCourseUpdate(eq(courseId), any(CourseUpdateReqDTO.class), eq(reason));
    }
}
