package com.mycompany.project.user.command.domain.repository;

import com.mycompany.project.user.command.domain.aggregate.TeacherDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherDetailRepository extends JpaRepository<TeacherDetail, Long> {
    // 기본 CRUD(save, findById 등)는 자동으로 제공됩니다.
    // 필요하다면 아래처럼 사용자 ID로 선생님 정보를 찾는 메서드를 추가할 수 있습니다.
    
    // Optional<TeacherDetail> findByUser_UserId(Long userId);
}