package com.mycompany.project.user.command.domain.repository;// com.mycompany.project.user.repository.StudentDetailRepository

import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentDetailRepository extends JpaRepository<StudentDetail, Long> {

}