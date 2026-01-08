package com.mycompany.project.user.command.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.user.command.service.UserCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자 관리 (Command)", description = "사용자 등록/수정/삭제 등 상태 변경 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final UserCommandService userCommandService;

  @Operation(summary = "사용자 대량 등록 (CSV)", description = "CSV 파일을 업로드하여 사용자를 일괄 등록합니다.\n헤더 필수: email, password, name, role, birthDate")
  @PostMapping(value = "/batch/import", consumes = "multipart/form-data")
  public ApiResponse<String> importUsersBatch(@RequestPart("file") MultipartFile file) {
    int count = userCommandService.importUser(file);
    return ApiResponse.success(count + "명 등록 완료");
  }

  @Operation(summary = "사용자 대량 수정 (CSV)", description = "이메일을 기준으로 기존 사용자 정보를 일괄 수정합니다.\n필수 Header: email. 선택 Header: name, role, birthDate 등")
  @PutMapping(value = "/batch/update", consumes = "multipart/form-data")
  public ApiResponse<String> updateUsersBatch(@RequestPart("file") MultipartFile file) {
    int count = userCommandService.updateUsersInBatch(file);
    return ApiResponse.success(count + "명 수정 완료");
  }
}
