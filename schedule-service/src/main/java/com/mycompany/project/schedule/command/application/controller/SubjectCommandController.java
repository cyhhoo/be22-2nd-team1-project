package com.mycompany.project.schedule.command.application.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.schedule.command.application.dto.SubjectCreateRequest;
import com.mycompany.project.schedule.command.application.service.SubjectCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Subject Management (Command)", description = "Subject registration and deletion API")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectCommandController {

    private final SubjectCommandService subjectCommandService;

    @Operation(summary = "Register subject", description = "Register a new subject.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> createSubject(@RequestBody SubjectCreateRequest request) {
        Long subjectId = subjectCommandService.createSubject(request);
        return ResponseEntity.ok(ApiResponse.success(subjectId));
    }

    @Operation(summary = "Delete subject", description = "Delete an existing subject.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSubject(@PathVariable Long id) {
        subjectCommandService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully"));
    }
}
