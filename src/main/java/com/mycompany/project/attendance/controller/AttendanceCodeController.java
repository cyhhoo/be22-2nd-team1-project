package com.mycompany.project.attendance.controller;

import com.mycompany.project.attendance.dto.response.AttendanceCodeResponse;
import com.mycompany.project.attendance.service.AttendanceCodeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 이 클래스는 REST API 컨트롤러다. (View가 아니라 JSON 응답을 반환)
@RequiredArgsConstructor // final 필드들을 생성자로 자동 주입한다. (생성자 DI)
@RequestMapping("/api/attendance/codes") // 이 컨트롤러의 공통 URL 시작 경로
public class AttendanceCodeController {

    private final AttendanceCodeQueryService attendanceCodeQueryService;
    // 출결 코드(출석/지각/결석 등) "조회(읽기)" 작업을 담당하는 서비스

    @GetMapping("/{attendanceCodeId}") // GET /api/attendance/codes/{attendanceCodeId}
    public AttendanceCodeResponse findById(@PathVariable Long attendanceCodeId) {
        // @PathVariable: URL 경로의 {attendanceCodeId} 값을 Long으로 받아온다.
        // 단건 조회 로직은 QueryService에 위임하고 결과를 DTO로 반환한다.
        return attendanceCodeQueryService.findById(attendanceCodeId);
    }

    @GetMapping // GET /api/attendance/codes
    public List<AttendanceCodeResponse> findAll(@RequestParam(required = false) Boolean activeOnly) {
        // @RequestParam: 쿼리스트링 파라미터를 받는다.
        // required=false: 파라미터가 없어도 요청이 에러가 나지 않는다(null로 들어옴)
        // activeOnly=true면 사용 중인 코드만 반환한다.
        // 예: /api/attendance/codes?activeOnly=true

        return attendanceCodeQueryService.findAll(activeOnly);
        // 전체 목록 또는 활성 코드만 조회하는 로직을 서비스에 위임하고 리스트를 반환한다.
    }
}
