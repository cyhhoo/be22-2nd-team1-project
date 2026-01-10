package com.mycompany.project.user.repository;

import com.mycompany.project.user.command.domain.aggregate.TeacherDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherDetailRepository extends JpaRepository<TeacherDetail, Long> {
}
