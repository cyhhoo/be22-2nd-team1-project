package com.mycompany.project.user.command.domain.repository;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name); // 과목명으로 조회
}