package com.mycompany.project.course.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.course.mapper.CourseMapper;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
}
