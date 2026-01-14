package com.mycompany.project.enrollment.repository;

import com.mycompany.project.enrollment.query.dto.CartListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper {

  // 내 장바구니 목록 조회
  List<CartListResponse> selectCartListByUserId(@Param("userId") Long userId);

  @Select("SELECT course FROM tbl_cart WHERE student_detail_id = #{studentId}")
  List<Long> findCourseIdsByStudentId(@Param("studentId") Long studentId);
}