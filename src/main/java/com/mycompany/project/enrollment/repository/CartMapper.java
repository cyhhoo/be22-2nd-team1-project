package com.mycompany.project.enrollment.repository;

import com.mycompany.project.enrollment.query.dto.CartListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {

  // 내 장바구니 목록 조회 (현재 신청 인원, 정원 포함 -> 마감 임박 확인용)
  List<CartListResponse> selectCartListByStudentId(@Param("studentId") Long studentId);
}
