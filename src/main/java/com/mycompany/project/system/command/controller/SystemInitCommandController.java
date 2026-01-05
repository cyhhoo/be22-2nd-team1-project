package com.mycompany.project.system.command.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "시스템 초기화 (System Initialization)", description = "초기 데이터 적재를 위한 관리자 전용 API")
@RestController
@RequestMapping("/api/system/init")
public class SystemInitCommandController {

    /*
     * [구현 가이드: 보안 주의]
     * 이 Controller는 시스템 초기 세팅 시에만 사용되므로,
     * 강력한 권한 체크(ROLE_ADMIN 등)가 반드시 선행되어야 합니다.
     * private final SystemInitService systemInitService;
     */

    @Operation(summary = "초기 사용자 대량 등록", description = "CSV 파일 등을 통해 사용자 계정을 일괄 생성합니다.")
    @PostMapping("/users")
    public void importUsers() {
        /*
         * [구현 가이드: 사용자 대량 등록 로직]
         * 1. 업로드된 CSV 파일(또는 요청 본문)을 읽습니다. (형식: 이메일, 이름, 초기비밀번호, 권한, 생년월일, 인증코드)
         * 2. 파일 파싱 및 루프 처리:
         * 각 라인에 대해:
         * 가. 필수값 유효성 검사 (이메일 형식이 맞는지 등).
         * 나. 비밀번호 암호화: BCryptPasswordEncoder 등을 사용하여 비밀번호를 해싱합니다.
         * 다. User 엔티티 생성.
         * 라. 이메일 중복 체크: 이미 존재하는 이메일이면 해당 라인은 건너뛰거나(Skip) 실패 로그를 기록하고 계속 진행합니다.
         * 마. DB 저장 (Batch Insert 권장).
         */
    }

    @Operation(summary = "초기 학사 데이터 등록", description = "CSV 파일 등을 통해 강좌 기초 데이터를 일괄 생성합니다.")
    @PostMapping("/courses")
    public void importCourses() {
        /*
         * [구현 가이드: 강좌 데이터 대량 등록 로직]
         * 1. 업로드된 CSV 파일(강의명, 교사ID 등)을 읽습니다.
         * 2. 데이터 무결성 검증:
         * - CSV에 명시된 교사ID가 실제 DB(User/Teacher 테이블)에 존재하는지 확인해야 합니다.
         * - 존재하지 않는 교사ID라면 에러 처리하거나 해당 라인 Skip.
         * 3. Course 엔티티 생성 후 DB 저장.
         */
    }
}
