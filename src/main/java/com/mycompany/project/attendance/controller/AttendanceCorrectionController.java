package com.mycompany.project.attendance.controller;

import com.mycompany.project.attendance.dto.request.CorrectionCreateRequest;
import com.mycompany.project.attendance.dto.request.CorrectionDecideRequest;
import com.mycompany.project.attendance.dto.request.CorrectionSearchRequest;
import com.mycompany.project.attendance.dto.response.CorrectionResponse;
import com.mycompany.project.attendance.service.AttendanceCorrectionCommandService;
import com.mycompany.project.attendance.service.AttendanceCorrectionQueryService;
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

@RestController // JSON으로 요청/응답 처리하는 컨트롤러
@RequiredArgsConstructor // final 필드 생성자 주입(Autowired 대신)
@RequestMapping("/api/attendance/corrections") // 정정요청 API 공통 경로
public class AttendanceCorrectionController {

    // 정정요청 생성/처리(승인/반려) 같은 "상태가 바뀌는 작업" 담당
    private final AttendanceCorrectionCommandService attendanceCorrectionCommandService;

    // 정정요청 조회(단건/목록) 담당
    private final AttendanceCorrectionQueryService attendanceCorrectionQueryService;

    @PostMapping // POST /api/attendance/corrections
    public ResponseEntity<ApiResponse<Void>> create(@RequestBody CorrectionCreateRequest request) {
        // 정정요청 생성 규칙(서비스에서 검증):
        // - 요청자는 교사여야 함(ROLE_TEACHER)
        // - 해당 출결이 "확정(CONFIRMED)" 또는 "마감(CLOSED)" 된 건만 정정요청 가능(정책 기준)
        // - 동일 출결(attendance_id)에 PENDING 요청이 이미 있으면 중복 생성 불가
        // - 요청 출결코드가 존재하고 활성 상태여야 함
        attendanceCorrectionCommandService.createCorrectionRequest(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/decide") // POST /api/attendance/corrections/decide
    public ResponseEntity<ApiResponse<Void>> decide(@RequestBody CorrectionDecideRequest request) {
        // 정정요청 처리 규칙(서비스에서 검증):
        // - 관리자가 처리해야 함(ROLE_ADMIN)
        // - 승인(approved=true)이면 출결(attendance)의 출결코드를 즉시 반영
        // - 반려(approved=false)이면 adminComment(반려 사유) 필수
        // - 처리 결과는 정정요청 상태(APPROVED/REJECTED)로 저장
        attendanceCorrectionCommandService.decideCorrectionRequest(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{requestId}") // GET /api/attendance/corrections/{requestId}
    public ResponseEntity<ApiResponse<CorrectionResponse>> findById(@PathVariable Long requestId) {
        // 정정요청 단건 조회
        // (QueryService에서 필요한 조인/응답 매핑을 해서 Response DTO로 반환)
        CorrectionResponse data = attendanceCorrectionQueryService.findById(requestId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping // GET /api/attendance/corrections?... (검색 조건)
    public ResponseEntity<ApiResponse<List<CorrectionResponse>>> search(@ModelAttribute CorrectionSearchRequest request) {
        // @ModelAttribute:
        // - GET 쿼리스트링 파라미터들을 CorrectionSearchRequest 필드에 자동 바인딩
        // 예) /api/attendance/corrections?status=PENDING&courseId=1&from=2026-01-01&to=2026-01-31
        List<CorrectionResponse> data = attendanceCorrectionQueryService.search(request);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
