package com.mycompany.project.user.command.domain.repository;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentDetailRepository extends JpaRepository<StudentDetail, Long> {
}