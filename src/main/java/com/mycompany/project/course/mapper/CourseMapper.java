package com.mycompany.project.course.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseMapper {
    /**
     * 교사의 해당 학기/요일/교시에 겹치는 스케줄 개수 조회 (폐강/반려 제외)
     */
    int countTeacherSchedule(@Param("academicYearId") Long academicYearId,
            @Param("teacherDetailId") Long teacherDetailId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("period") Integer period);

    /**
     * 강의실의 해당 학기/요일/교시에 겹치는 스케줄 개수 조회 (폐강/반려 제외)
     */
    int countClassroomSchedule(@Param("academicYearId") Long academicYearId,
            @Param("classroom") String classroom,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("period") Integer period);
}
