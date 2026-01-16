package com.mycompany.project.attendance.command.application.service;

import com.mycompany.project.attendance.command.application.dto.AttendanceClosureRequest;
import com.mycompany.project.attendance.command.domain.aggregate.AttendanceClosure;
import com.mycompany.project.attendance.command.domain.repository.AttendanceClosureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceClosureCommandService {

    private final AttendanceClosureRepository attendanceClosureRepository;

    /**
     * Close attendances for a specific scope
     */
    @Transactional
    public void closeAttendances(AttendanceClosureRequest request) {
        AttendanceClosure closure = AttendanceClosure.builder()
                .academicYearId(request.getAcademicYearId())
                .scopeType(request.getScopeType())
                .scopeValue(request.getScopeValue())
                .grade(request.getGrade())
                .classNo(request.getClassNo())
                .courseId(request.getCourseId())
                .userId(request.getUserId())
                .build();

        attendanceClosureRepository.save(java.util.Objects.requireNonNull(closure));
        // Logic to update actual attendance records to CLOSED state would go here
    }
}
