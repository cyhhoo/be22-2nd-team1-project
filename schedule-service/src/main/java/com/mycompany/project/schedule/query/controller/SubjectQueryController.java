package com.mycompany.project.schedule.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.schedule.query.dto.SubjectResponse;
import com.mycompany.project.schedule.query.service.SubjectQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Subject Management (Query)", description = "Subject query API")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectQueryController {

    private final SubjectQueryService subjectQueryService;

    @Operation(summary = "Get all subjects", description = "Retrieve all registered subjects.")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAllSubjects() {
        return ResponseEntity.ok(ApiResponse.success(subjectQueryService.getAllSubjects()));
    }

    @Operation(summary = "Get subject detail", description = "Retrieve information of a specific subject.")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubject(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subjectQueryService.getSubjectById(id)));
    }
}
