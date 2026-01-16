package com.mycompany.project.common.command.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.common.response.FileResponse;
import com.mycompany.project.common.command.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/common/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "File upload", description = "Upload image or other files and receive URL")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FileResponse>> uploadFile(@RequestPart("file") MultipartFile file) {
        FileResponse fileDto = fileService.uploadFile(file);
        return ResponseEntity.ok(ApiResponse.success(fileDto));
    }
}