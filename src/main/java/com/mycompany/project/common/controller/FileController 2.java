package com.mycompany.project.common.controller;

import com.mycompany.project.common.dto.ApiDTO;
import com.mycompany.project.common.dto.FileDTO;
import com.mycompany.project.common.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/common/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "파일 업로드", description = "이미지 등 파일을 업로드하고 URL을 반환받습니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiDTO<FileDTO> uploadFile(@RequestPart("file") MultipartFile file) {
        FileDTO fileDto = fileService.uploadFile(file);
        return ApiDTO.success(fileDto);
    }
}