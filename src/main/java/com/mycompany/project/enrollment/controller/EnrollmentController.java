package com.mycompany.project.enrollment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "수강 신청 (Enrollment)", description = "수강 신청 및 취소 관련 API")
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    /*
     * [구현 가이드: Service 의존성 주입]
     * EnrollmentService를 주입받아 비즈니스 로직을 위임해야 합니다.
     * private final EnrollmentService enrollmentService;
     */

    @Operation(summary = "강좌 수강 신청", description = "학생이 특정 강좌를 수강 신청합니다. (동시성 제어 적용 필수)")
    @PostMapping("/{courseId}")
    public void enroll(@PathVariable Long courseId) {
        /*
         * [구현 가이드: 수강 신청 로직 순서]
         * 1. 로그인된 사용자(학생)의 정보를 가져옵니다. (Spring Security Context 등 활용)
         * 2. DB 레벨의 Lock(비관적 락 등)을 사용하여 해당 강좌(Course) 정보를 조회합니다.
         * - 목적: 동시 다발적인 신청 요청 시 수강 인원 데이터의 정합성을 보장하기 위함.
         * - 쿼리 예시: select * from tbl_course where course_id = ? for update
         * 3. validation:
         * - 현재 수강 인원이 최대 정원보다 작은지 확인합니다. (가득 찼다면 FullCapacityException 발생)
         * - 사용자가 이미 해당 강좌를 신청했는지 확인합니다. (중복 신청 시 AlreadyEnrolledException 발생)
         * 4. 수강 내역(Enrollment)을 생성하여 저장합니다 (Insert).
         * 5. 강좌(Course)의 현재 수강 인원(current_count)을 1 증가시킵니다 (Update).
         * 6. (선택) 처리가 완료되면 성공 응답을 반환합니다.
         */
    }

    @Operation(summary = "수강 신청 취소", description = "학생이 신청한 강좌를 취소합니다.")
    @DeleteMapping("/{courseId}")
    public void cancel(@PathVariable Long courseId) {
        /*
         * [구현 가이드: 수강 취소 로직 순서]
         * 1. 로그인된 사용자(학생) 정보와 courseId를 이용하여 수강 내역 조회 및 권한을 확인합니다.
         * 2. 수강 내역(Enrollment)이 존재하면 삭제합니다 (Hard Delete 권장).
         * 3. 강좌(Course)의 현재 수강 인원(current_count)을 1 감소시킵니다 (Update).
         * - 주의: 인원 감소 시 0보다 작아지지 않도록 체크 로직이 필요할 수 있습니다.
         */
    }
}
