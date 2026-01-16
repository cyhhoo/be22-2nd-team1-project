package com.mycompany.project.course.query.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper {
        /**
         * 援먯궗???대떦 ?숆린/?붿씪/援먯떆??寃뱀튂???ㅼ?以?媛쒖닔 議고쉶 (?먭컯/諛섎젮 ?쒖쇅)
         */
        int countTeacherSchedule(
                        @Param("academicYearId") Long academicYearId,
                        @Param("teacherDetailId") Long teacherDetailId,
                        @Param("dayOfWeek") String dayOfWeek,
                        @Param("period") Integer period);

        /**
         * 媛뺤쓽?ㅼ쓽 ?대떦 ?숆린/?붿씪/援먯떆??寃뱀튂???ㅼ?以?媛쒖닔 議고쉶 (?먭컯/諛섎젮 ?쒖쇅)
         */
        int countClassroomSchedule(
                        @Param("academicYearId") Long academicYearId,
                        @Param("classroom") String classroom,
                        @Param("dayOfWeek") String dayOfWeek,
                        @Param("period") Integer period);

        /**
         * ?숈깮???대떦 ?숆린/?붿씪/援먯떆??寃뱀튂???섍컯 ?댁뿭 議고쉶 (?먭컯/諛섎젮 ?쒖쇅)
         * 湲곗〈 ?섏뾽??ID, 媛뺤쥖紐? ?댁닔援щ텇(?꾩닔/?좏깮) 諛섑솚
         */
        List<Map<String, Object>> findConflictingEnrollments(
                        @Param("academicYearId") Long academicYearId,
                        @Param("studentId") Long studentId,
                        @Param("dayOfWeek") String dayOfWeek,
                        @Param("period") Integer period);

        /**
         * 援먯궗??二쇨컙 ?쒓컙??議고쉶 (?대떦 ?숆린 紐⑤뱺 媛뺤쥖)
         * 諛섑솚: ?붿씪, 援먯떆, 媛뺤쥖紐? 媛뺤쓽?? 媛뺤쥖ID, ?댁닔援щ텇
         */
        List<Map<String, Object>> findTeacherTimetable(
                        @Param("academicYearId") Long academicYearId,
                        @Param("teacherDetailId") Long teacherDetailId);
}
