package com.mycompany.project.reservation.query.service;

import com.mycompany.project.reservation.query.dto.ReservationDTO;
import com.mycompany.project.reservation.query.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
  private final SqlSessionTemplate sqlSessionTemplate;

  public ReservationService(SqlSessionTemplate sqlSessionTemplate) {
    this.sqlSessionTemplate = sqlSessionTemplate;
  }

  public List<ReservationDTO> selectAvailableFacilities() {
    // TODO: Implement logic
    return List.of();
  }

}
