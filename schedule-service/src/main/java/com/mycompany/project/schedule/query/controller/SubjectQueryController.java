package com.mycompany.project.schedule.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.schedule.query.dto.SubjectResponse;
import com.mycompany.project.schedule.query.service.SubjectQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "과목 관리 (Query)", description = "과목 조회 API")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectQueryController {

    private final SubjectQueryService subjectQueryService;

    @Operation(summary = "전체 과목 목록 조회", description = "등록된 모든 과목을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAllSubjects() {
        return ResponseEntity.ok(ApiResponse.success(subjectQueryService.getAllSubjects()));
    }

    @Operation(summary = "과목 상세 조회", description = "특정 과목의 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubject(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subjectQueryService.getSubjectById(id)));
    }
}
