package com.mycompany.project.enrollment.command.domain.repository;

import com.mycompany.project.enrollment.query.dto.CartListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper {

  /**
   * Inquiry of cart list by User ID
   */
  List<CartListResponse> selectCartListByUserId(@Param("userId") Long userId);

  @Select("SELECT course FROM tbl_cart WHERE student_detail_id = #{studentId}")
  List<Long> findCourseIdsByStudentId(@Param("studentId") Long studentId);
}