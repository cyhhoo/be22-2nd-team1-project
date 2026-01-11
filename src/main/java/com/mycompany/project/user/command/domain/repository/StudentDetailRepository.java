package com.mycompany.project.user.command.domain.repository;// com.mycompany.project.user.repository.StudentDetailRepository

import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentDetailRepository extends JpaRepository<StudentDetail, Long> {
    // ... 기존 메서드들 ...

  // [수정 전] 에러 발생 (JPA가 User.id를 찾으려고 시도함)
  // Optional<StudentDetail> findByUserId(Long userId);

  // [수정 후] JPQL을 사용하여 "user 객체의 userId 필드"를 정확하게 가리킴
  @Query("SELECT s FROM StudentDetail s WHERE s.user.userId = :userId")
  Optional<StudentDetail> findByUserId(@Param("userId") Long userId);
}