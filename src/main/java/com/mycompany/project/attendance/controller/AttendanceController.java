package com.mycompany.project.attendance.controller;

// ====== 요청(Request) DTO ======
import com.mycompany.project.attendance.dto.request.AttendanceConfirmRequest;   // 확정 요청 바디(JSON) DTO
import com.mycompany.project.attendance.dto.request.AttendanceCreateRequest;    // 출석부 생성 요청 바디(JSON) DTO
import com.mycompany.project.attendance.dto.request.AttendanceSearchRequest;    // 조회 조건(쿼리스트링) DTO
import com.mycompany.project.attendance.dto.request.AttendanceUpdateRequest;    // 출결 저장(여러 학생) 요청 바디 DTO

// ====== 응답(Response) DTO ======
import com.mycompany.project.attendance.dto.response.AttendanceListResponse;    // 목록/행 단위 응답 DTO
import com.mycompany.project.attendance.dto.response.AttendanceResponse;        // 상세(단건) 응답 DTO

// ====== 서비스(비즈니스 로직) ======
import com.mycompany.project.attendance.service.AttendanceCommandService;       // CUD(쓰기) 담당: 생성/저장/확정
import com.mycompany.project.attendance.service.AttendanceQueryService;         // Read(조회) 담당: 상세/목록 조회

// ====== 롬복/스프링 MVC ======
import com.mycompany.project.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;                                          // final 필드 생성자 자동 생성
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;                      // GET 요청 매핑
import org.springframework.web.bind.annotation.ModelAttribute;                  // 쿼리스트링 → DTO 바인딩
import org.springframework.web.bind.annotation.PathVariable;                    // URL 경로 변수 바인딩
import org.springframework.web.bind.annotation.PostMapping;                     // POST 요청 매핑
import org.springframework.web.bind.annotation.RequestBody;                     // JSON 바디 → DTO 바인딩
import org.springframework.web.bind.annotation.RequestMapping;                  // 클래스 공통 URL 매핑
import org.springframework.web.bind.annotation.RestController;                  // JSON 응답 컨트롤러(Controller + ResponseBody)

// ====== 스웨거(OpenAPI) ======
import io.swagger.v3.oas.annotations.Operation;                                 // API 문서: 메서드 설명
import io.swagger.v3.oas.annotations.tags.Tag;                                  // API 문서: 컨트롤러 그룹

import java.util.List;

@Tag(
        name = "출결 관리 (Attendance)",
        description = "출석부 생성 및 마감 관리 API"
) // Swagger에서 이 컨트롤러를 하나의 그룹으로 묶어준다.
@RestController
@RequiredArgsConstructor // final 필드(서비스)들을 생성자로 주입하기 위해 사용
@RequestMapping("/api/v1/attendance") // 이 컨트롤러의 기본 주소(공통 prefix)
public class AttendanceController {

    // "쓰기" 로직 담당 서비스(JPA 기반 Command).
    private final AttendanceCommandService attendanceCommandService;

    // "조회" 로직 담당 서비스(MyBatis 기반 Query).
    private final AttendanceQueryService attendanceQueryService;

    @Operation(
            summary = "출석부 자동 생성 (조회 겸 생성)",
            description = "특정 강좌의 해당 날짜/교시 출석 데이터를 조회하거나 없으면 자동 생성합니다."
    )
    @PostMapping // POST /api/attendance
    public ResponseEntity<ApiResponse<List<AttendanceListResponse>>> generateAttendanceSheet(
            @RequestBody AttendanceCreateRequest request
    ) {
        List<AttendanceListResponse> data = attendanceCommandService.generateAttendances(request);
        // request: courseId, classDate, period, userId(교사) 등이 들어온다.
        // 흐름:
        // 1) 이미 출결이 있으면 그대로 조회 결과를 반환
        // 2) 없으면 enrollment(수강신청) 기준으로 출결을 생성한 뒤 조회 결과를 반환
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(
            summary = "출결 저장(등록/수정)",
            description = "교사가 출결 정보를 저장하거나 수정합니다."
    )
    @PostMapping("/save") // POST /api/attendance/save
    public ResponseEntity<ApiResponse<Void>> saveAttendance(@RequestBody AttendanceUpdateRequest request) {
        attendanceCommandService.saveAttendances(request);
        // request.items에 여러 학생(enrollment) 출결이 들어올 수 있다.
        // 중요한 규칙:
        // - CONFIRMED(확정) / CLOSED(마감) 상태는 수정이 막혀야 한다.
        // - 저장은 SAVED 상태로 남긴다(담임 확정 전 단계).
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "출결 확정",
            description = "담임/책임교사가 출결을 확정합니다."
    )
    @PostMapping("/confirm") // POST /api/attendance/confirm
    public ResponseEntity<ApiResponse<Void>> confirmAttendance(@RequestBody AttendanceConfirmRequest request) {
        attendanceCommandService.confirmAttendances(request);
        // 확정은 "담임" 또는 정책상 허용된 권한만 가능해야 한다.
        // 또, 미입력 출결(해당 날짜/교시에 attendance가 없는 학생)이 있으면 확정이 막혀야 한다.
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    @Operation(
            summary = "출결 상세 조회",
            description = "출결 단건 상세를 조회합니다."
    )
    @GetMapping("/{attendanceId}") // GET /api/attendance/{attendanceId}
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendance(@PathVariable Long attendanceId) {
        AttendanceResponse data = attendanceQueryService.findById(attendanceId);
        // 경로로 들어온 attendanceId로 단건 조회한다.
        // (QueryService 쪽에서 MyBatis로 join해서 상세 응답을 만들 가능성이 높음)
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(
            summary = "출결 목록 조회",
            description = "조건에 따라 출결 목록을 조회합니다."
    )
    @GetMapping// GET /api/attendance?courseId=...&fromDate=...&toDate=...&period=...
    public ResponseEntity<ApiResponse<List<AttendanceListResponse>>> search(@ModelAttribute AttendanceSearchRequest request) {
        List<AttendanceListResponse> data = attendanceQueryService.search(request);
        // @ModelAttribute:
        // - GET 쿼리스트링 값을 AttendanceSearchRequest 필드에 자동으로 채워준다.
        // - 예: ?courseId=1&period=2&fromDate=2026-01-01&toDate=2026-01-31
        return ResponseEntity.ok(ApiResponse.success(data));

    }
}
