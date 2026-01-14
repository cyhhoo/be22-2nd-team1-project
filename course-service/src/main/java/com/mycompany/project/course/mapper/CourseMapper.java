package com.mycompany.project.course.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper {
        /**
         * 교사의 해당 학기/요일/교시에 겹치는 스케줄 개수 조회 (폐강/반려 제외)
         */
        int countTeacherSchedule(
                        @Param("academicYearId") Long academicYearId,
                        @Param("teacherDetailId") Long teacherDetailId,
                        @Param("dayOfWeek") String dayOfWeek,
                        @Param("period") Integer period);

        /**
         * 강의실의 해당 학기/요일/교시에 겹치는 스케줄 개수 조회 (폐강/반려 제외)
         */
        int countClassroomSchedule(
                        @Param("academicYearId") Long academicYearId,
                        @Param("classroom") String classroom,
                        @Param("dayOfWeek") String dayOfWeek,
                        @Param("period") Integer period);

        /**
         * 학생의 해당 학기/요일/교시에 겹치는 수강 내역 조회 (폐강/반려 제외)
         * 기존 수업의 ID, 강좌명, 이수구분(필수/선택) 반환
         */
        List<Map<String, Object>> findConflictingEnrollments(
                        @Param("academicYearId") Long academicYearId,
                        @Param("studentId") Long studentId,
                        @Param("dayOfWeek") String dayOfWeek,
                        @Param("period") Integer period);

        /**
         * 교사의 주간 시간표 조회 (해당 학기 모든 강좌)
         * 반환: 요일, 교시, 강좌명, 강의실, 강좌ID, 이수구분
         */
        List<Map<String, Object>> findTeacherTimetable(
                        @Param("academicYearId") Long academicYearId,
                        @Param("teacherDetailId") Long teacherDetailId);

        /**
         * 교사별 강좌 목록 조회 (페이징)
         * 
         * @return CourseListResDTO 목록
         */
        List<com.mycompany.project.course.dto.CourseListResDTO> findCourseListByTeacher(
                        @Param("teacherId") Long teacherId,
                        @Param("limit") int limit,
                        @Param("offset") int offset);

        /**
         * 교사별 강좌 목록 전체 개수 조회 (페이징용)
         */
        long countCourseListByTeacher(@Param("teacherId") Long teacherId);

        /**
         * 전체 강좌 목록 조회 (관리자, 페이징)
         * 
         * @return CourseListResDTO 목록
         */
        List<com.mycompany.project.course.dto.CourseListResDTO> findAllCourseList(
                        @Param("limit") int limit,
                        @Param("offset") int offset);

        /**
         * 전체 강좌 목록 전체 개수 조회 (페이징용)
         */
        long countAllCourseList();

        /**
         * 수강생 상세 정보 조회 (강좌 ID + 학생 User ID)
         * 출결 통계, 메모 등 포함
         * 
         * @return StudentDetailResDTO 또는 null
         */
        com.mycompany.project.course.dto.StudentDetailResDTO findStudentDetailByCourseAndStudent(
                        @Param("courseId") Long courseId,
                        @Param("studentId") Long studentId);
}
