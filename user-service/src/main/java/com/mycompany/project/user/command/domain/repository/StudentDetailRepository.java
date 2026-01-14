package com.mycompany.project.user.command.domain.repository;

import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface StudentDetailRepository extends JpaRepository<StudentDetail, Long> {

        List<StudentDetail> findByIdInAndGradeAndClassNo(List<Long> studentIds,
                        Integer studentGrade,
                        String studentClassNo);

        List<StudentDetail> findByIdInAndGrade(List<Long> studentIds,
                        Integer studentGrade);

        long countByIdInAndGradeAndClassNo(List<Long> studentIds,
                        Integer studentGrade,
                        String studentClassNo);

        List<StudentDetail> findByGradeAndClassNo(Integer grade,
                        String classNo);

        default Optional<StudentDetail> findByUserId(Long userId) {
                return findById(userId);
        }
}