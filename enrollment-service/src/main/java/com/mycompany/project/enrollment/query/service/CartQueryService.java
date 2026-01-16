package com.mycompany.project.enrollment.query.service;

import com.mycompany.project.enrollment.query.dto.CartListResponse;
import com.mycompany.project.enrollment.command.domain.repository.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartQueryService {

  private final CartMapper cartMapper;

  public List<CartListResponse> getMyCartList(Long userId) {
    return cartMapper.selectCartListByUserId(userId);
  }

}
