package com.mycompany.project.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "수강 관리 (Course Management)", description = "강좌 개설, 조회 및 시간표 관련 API")
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    /*
     * [구현 가이드: Service 의존성 주입]
     * CourseService 등을 주입받아야 합니다.
     * private final CourseService courseService;
     */

    @Operation(summary = "강좌 개설", description = "새로운 강좌를 개설합니다. (시간/강의실 중복 체크 포함)")
    @PostMapping
    public void createCourse() {
        /*
         * [구현 가이드: 강좌 개설 로직 순서]
         * 1. 요청 파라미터(강의명, 교사ID, 요일, 교시, 강의실ID 등)를 수신합니다. (DTO 사용 권장)
         * 2. 유효성 검사 (중복 체크 - 매우 중요):
         * 가. 교사 중복 체크: 해당 교사가 요청된 요일/교시에 이미 다른 수업이 있는지 확인합니다.
         * 나. 강의실 중복 체크: 해당 강의실이 요청된 요일/교시에 이미 예약/사용 중인지 확인합니다.
         * - 중복 발견 시 TimeConflictException 등을 발생시켜 처리를 중단합니다.
         * 3. 검사 통과 시, 새로운 강좌(Course) 정보를 DB에 저장합니다.
         */
    }

    @Operation(summary = "시간표 격자 조회", description = "지정된 학기/사용자의 시간표를 격자(Grid) 형태로 조회합니다.")
    @GetMapping("/timetable")
    public void getTimetable(@RequestParam String semester, @RequestParam Long userId) {
        /*
         * [구현 가이드: 시간표 조회 로직 (MyBatis 사용 권장)]
         * 1. semester와 userId를 조건으로 수강 신청된 강좌 목록을 조회합니다.
         * 2. 데이터를 '요일(행) x 교시(열)' 또는 '교시(행) x 요일(열)' 형태의 2차원 구조로 반환해야 합니다.
         * 3. 쿼리 작성 팁 (Pivot):
         * - MyBatis XML 매퍼에서 CASE WHEN 구문을 활용하여 데이터를 회전시킬 수 있습니다.
         * - 예시:
         * SELECT
         * MAX(CASE WHEN day = 'MON' THEN course_name ELSE NULL END) AS mon_course,
         * MAX(CASE WHEN day = 'TUE' THEN course_name ELSE NULL END) AS tue_course,
         * ...
         * FROM ...
         * GROUP BY period
         * 4. 조회된 데이터를 TimeTableDTO 등으로 매핑하여 반환합니다.
         */
    }
}
