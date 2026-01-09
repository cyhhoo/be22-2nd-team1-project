package com.mycompany.project.user.repository;

import com.mycompany.project.user.entity.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface StudentDetailRepository extends JpaRepository<StudentDetail, Long> {
    long countByStudentIdInAndStudentGradeAndStudentClassNo(Collection<Long> studentIds, Integer studentGrade, String studentClassNo);

    List<StudentDetail> findByStudentIdInAndStudentGradeAndStudentClassNo(Collection<Long> studentIds, Integer studentGrade, String studentClassNo);

    List<StudentDetail> findByStudentIdInAndStudentGrade(Collection<Long> studentIds, Integer studentGrade);
}
