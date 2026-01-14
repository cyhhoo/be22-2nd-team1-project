package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.request.AttendanceSearchRequest;
import com.mycompany.project.attendance.dto.response.AttendanceListResponse;
import com.mycompany.project.attendance.dto.response.AttendanceResponse;
import com.mycompany.project.attendance.mapper.AttendanceQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 출결 "조회(Query)" 전용 서비스(MyBatis Mapper 호출 역할)
@RequiredArgsConstructor // 생성자 주입(final 필드 자동 생성)
public class AttendanceQueryService {

    // 출결 조회용 MyBatis Mapper
    private final AttendanceQueryMapper attendanceQueryMapper;
    private final com.mycompany.project.attendance.client.EnrollmentClient enrollmentClient;

    /**
     * 출결 단건(상세) 조회
     * - attendance_id 기준으로 1건 조회해서 Response로 반환
     */
    public AttendanceResponse findById(Long attendanceId) {
        return attendanceQueryMapper.findById(attendanceId);
    }

    /**
     * 출결 목록 조회(조건 검색)
     * - 강좌, 기간(from~to), 교시, 학년/반, 학생 등 조건으로 필터링해서 목록 반환
     */
    public List<AttendanceListResponse> search(AttendanceSearchRequest cond) {
        if (cond.getCourseId() != null) {
            List<com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse> enrollments = enrollmentClient
                    .getInternalEnrollments(cond.getCourseId(),
                            com.mycompany.project.enrollment.entity.EnrollmentStatus.APPLIED.name());

            List<Long> ids = enrollments.stream()
                    .map(com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse::getEnrollmentId)
                    .toList();

            if (ids.isEmpty()) {
                // 수강생이 없으면 출결 내역도 없음
                return List.of();
            }
            cond.setEnrollmentIds(ids);
        }
        return attendanceQueryMapper.search(cond);
    }

    /**
     * 출결 페이징용 전체 개수 조회
     * - search 조건과 동일한 조건으로 count만 조회
     */
    public long count(AttendanceSearchRequest cond) {
        if (cond.getCourseId() != null) {
            if (cond.getEnrollmentIds() == null) {
                List<com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse> enrollments = enrollmentClient
                        .getInternalEnrollments(cond.getCourseId(),
                                com.mycompany.project.enrollment.entity.EnrollmentStatus.APPLIED.name());

                List<Long> ids = enrollments.stream()
                        .map(com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse::getEnrollmentId)
                        .toList();

                if (ids.isEmpty()) {
                    return 0;
                }
                cond.setEnrollmentIds(ids);
            }
        }
        return attendanceQueryMapper.count(cond);
    }
}
