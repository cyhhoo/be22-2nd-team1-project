package com.mycompany.project.enrollment.command.service;

import com.mycompany.project.enrollment.repository.CartMapper;
import com.mycompany.project.enrollment.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartCommandService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
}
