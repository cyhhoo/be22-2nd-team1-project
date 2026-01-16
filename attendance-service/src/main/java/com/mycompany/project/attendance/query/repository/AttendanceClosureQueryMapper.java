package com.mycompany.project.attendance.query.repository;

import com.mycompany.project.attendance.command.application.dto.ClosureSearchRequest;
import com.mycompany.project.attendance.query.application.dto.AttendanceClosureResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceClosureQueryMapper {

    /**
     * Get single closure detail
     */
    AttendanceClosureResponse findById(@Param("closureId") Long closureId);

    /**
     * Search closure history with conditions
     */
    List<AttendanceClosureResponse> search(ClosureSearchRequest cond);

    /**
     * Count total closures for pagination
     */
    long count(ClosureSearchRequest cond);
}
