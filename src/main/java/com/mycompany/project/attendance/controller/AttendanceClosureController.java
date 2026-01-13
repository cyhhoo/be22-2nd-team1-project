package com.mycompany.project.attendance.controller;

import com.mycompany.project.attendance.dto.request.AttendanceClosureRequest;
import com.mycompany.project.attendance.dto.request.ClosureSearchRequest;
import com.mycompany.project.attendance.dto.response.AttendanceClosureResponse;
import com.mycompany.project.attendance.service.AttendanceClosureCommandService;
import com.mycompany.project.attendance.service.AttendanceClosureQueryService;
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 이 클래스는 REST API 컨트롤러다. (View가 아니라 JSON 응답을 반환)
@RequiredArgsConstructor // final 필드들을 생성자로 자동 주입한다. (생성자 DI)
@RequestMapping("/api/attendance/closures") // 이 컨트롤러의 공통 URL 시작 경로
public class AttendanceClosureController {

    private final AttendanceClosureCommandService attendanceClosureCommandService;
    // 출결 마감처럼 "상태 변경(쓰기)" 작업을 담당하는 서비스

    private final AttendanceClosureQueryService attendanceClosureQueryService;
    // 출결 마감 관련 "조회(읽기)" 작업을 담당하는 서비스

    @PostMapping // POST /api/attendance/closures
    public ResponseEntity<ApiResponse<Void>> close(@RequestBody AttendanceClosureRequest request) {
        // @RequestBody: 요청 JSON 바디를 AttendanceClosureRequest DTO로 변환해서 받는다.
        // 마감 범위에 해당하는 출결(CONFIRMED)만 CLOSED로 전환하고 이력을 남긴다.
        // 실제 비즈니스 로직은 컨트롤러에서 하지 않고 서비스에 위임한다.
        attendanceClosureCommandService.closeAttendances(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{closureId}") // GET /api/attendance/closures/{closureId}
    public ResponseEntity<ApiResponse<AttendanceClosureResponse>> findById(@PathVariable Long closureId) {
        // @PathVariable: URL 경로의 {closureId} 값을 Long으로 받아온다.
        // 단건 조회 로직은 QueryService에 위임하고 결과를 DTO로 반환한다.
        AttendanceClosureResponse data = attendanceClosureQueryService.findById(closureId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping // GET /api/attendance/closures
    public ResponseEntity<ApiResponse<List<AttendanceClosureResponse>>> search(@ModelAttribute ClosureSearchRequest request) {
        // @ModelAttribute: 쿼리스트링 파라미터를 ClosureSearchRequest DTO로 바인딩한다.
        // 예: /api/attendance/closures?fromDate=2026-01-01&toDate=2026-01-31&status=CLOSED
        // 검색/목록 조회 로직은 QueryService에 위임하고 결과 리스트를 반환한다.
        List<AttendanceClosureResponse> data = attendanceClosureQueryService.search(request);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
