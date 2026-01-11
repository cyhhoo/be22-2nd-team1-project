package com.mycompany.project.course.repository;

import jakarta.persistence.LockModeType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.course.entity.Course;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

  // [핵심] 비관적 락(Pessimistic Lock) 적용
  // 이 메서드로 조회하면, 트랜잭션이 끝날 때까지 다른 사람은 이 강좌를 수정하지 못하고 대기합니다.
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select c from Course c where c.courseId = :id")
  Optional<Course> findByIdWithLock(@Param("id") Long id);

}
