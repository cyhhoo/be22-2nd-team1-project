package com.mycompany.project.schedule.query.mapper;

import com.mycompany.project.schedule.query.dto.ScheduleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper {

  List<ScheduleDTO> selectMonthlySchedules(@Param("year") int year, @Param("month")int month);

  List<ScheduleDTO> selectWeeklySchedules(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
