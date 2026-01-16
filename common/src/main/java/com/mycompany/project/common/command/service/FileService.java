package com.mycompany.project.common.command.service;

import com.mycompany.project.common.response.FileResponse;
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

    // File storage path: project root/uploads
    private final String uploadDir = "uploads/";

    public FileResponse uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        // 1. Create directory
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. Prevent filename collision (using UUID)
        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = Paths.get(uploadDir + savedFilename);

        // 3. Save file
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while saving file: " + e.getMessage());
        }

        // 4. Return result (expose URL as /uploads/filename)
        return FileResponse.builder()
                .originalFileName(originalFilename)
                .savedFileName(savedFilename)
                .filePath(filePath.toString())
                .url("/uploads/" + savedFilename)
                .build();
    }
}