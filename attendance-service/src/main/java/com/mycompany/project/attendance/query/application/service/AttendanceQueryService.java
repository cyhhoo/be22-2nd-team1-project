package com.mycompany.project.attendance.query.application.service;

import com.mycompany.project.attendance.command.application.dto.AttendanceSearchRequest;
import com.mycompany.project.attendance.query.application.dto.AttendanceListResponse;
import com.mycompany.project.attendance.query.application.dto.AttendanceResponse;
import com.mycompany.project.attendance.query.repository.AttendanceQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceQueryService {

    // Attendance query MyBatis Mapper
    private final AttendanceQueryMapper attendanceQueryMapper;
    private final com.mycompany.project.attendance.client.EnrollmentClient enrollmentClient;

    /**
     * Get single attendance detail
     * - Query by attendance_id and return Response
     */
    public AttendanceResponse findById(Long attendanceId) {
        return attendanceQueryMapper.findById(attendanceId);
    }

    /**
     * Search attendance list by conditions
     * - Filter by course, date range, period, grade/class, student, etc.
     */
    public List<AttendanceListResponse> search(AttendanceSearchRequest cond) {
        if (cond.getCourseId() != null) {
            List<com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse> enrollments = enrollmentClient
                    .getInternalEnrollments(cond.getCourseId(),
                            com.mycompany.project.common.enums.EnrollmentStatus.APPLIED.name());

            List<Long> ids = enrollments.stream()
                    .map(com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse::getEnrollmentId)
                    .toList();

            if (ids.isEmpty()) {
                // No enrolled students means no attendance records
                return List.of();
            }
            cond.setEnrollmentIds(ids);
        }
        return attendanceQueryMapper.search(cond);
    }

    /**
     * Count attendance records for pagination
     * - Same conditions as search, returns count only
     */
    public long count(AttendanceSearchRequest cond) {
        if (cond.getCourseId() != null) {
            if (cond.getEnrollmentIds() == null) {
                List<com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse> enrollments = enrollmentClient
                        .getInternalEnrollments(cond.getCourseId(),
                                com.mycompany.project.common.enums.EnrollmentStatus.APPLIED
                                        .name());

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
