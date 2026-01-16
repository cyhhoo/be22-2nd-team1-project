package com.mycompany.project.attendance.query.repository;

import com.mycompany.project.attendance.command.application.dto.CorrectionSearchRequest;
import com.mycompany.project.attendance.query.application.dto.CorrectionResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceCorrectionQueryMapper {

    /**
     * Get single correction request detail
     */
    CorrectionResponse findById(@Param("requestId") Long requestId);

    /**
     * Search correction requests with conditions
     */
    List<CorrectionResponse> search(CorrectionSearchRequest cond);

    /**
     * Count correction requests for pagination
     */
    long count(CorrectionSearchRequest cond);
}
