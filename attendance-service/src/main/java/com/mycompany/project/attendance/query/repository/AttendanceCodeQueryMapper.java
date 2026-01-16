package com.mycompany.project.attendance.query.repository;

import com.mycompany.project.attendance.query.application.dto.AttendanceCodeResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceCodeQueryMapper {

    /**
     * Get single attendance code detail
     */
    AttendanceCodeResponse findById(@Param("attendanceCodeId") Long attendanceCodeId);

    /**
     * Get all attendance codes
     * - if activeOnly is true, returns only active codes
     */
    List<AttendanceCodeResponse> findAll(@Param("activeOnly") Boolean activeOnly);
}
