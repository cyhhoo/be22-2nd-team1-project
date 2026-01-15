package com.mycompany.project.attendance.client;

import com.mycompany.project.attendance.client.dto.InternalAcademicYearResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "swcamp-schedule-service", url = "${gateway.url}")
public interface ScheduleClient {

    @GetMapping("/schedule/internal/academic-years/{academicYearId}")
    InternalAcademicYearResponse getInternalAcademicYear(@PathVariable("academicYearId") Long academicYearId);
}
