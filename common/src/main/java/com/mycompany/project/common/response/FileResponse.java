package com.mycompany.project.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private String originalFileName; // 사용자가 올린 원래 파일명
    private String savedFileName; // 서버에 저장된 유니크한 파일명 (UUID)
    private String filePath; // 파일이 저장된 실제 경로
    private String url; // 브라우저에서 접근할 수 있는 URL
}