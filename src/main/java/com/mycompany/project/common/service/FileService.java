package com.mycompany.project.common.service;

import com.mycompany.project.common.dto.FileDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    // 파일 저장 경로: 프로젝트 루트/uploads
    private final String uploadDir = "uploads/";

    public FileDTO uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 1. 디렉토리 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 파일명 중복 방지 (UUID 사용)
        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = Paths.get(uploadDir + savedFilename);

        // 3. 파일 저장
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 4. 결과 반환 (URL은 /uploads/파일명 형태로 접근)
        return FileDTO.builder()
                .originalFileName(originalFilename)
                .savedFileName(savedFilename)
                .filePath(filePath.toString())
                .url("/uploads/" + savedFilename)
                .build();
    }
}