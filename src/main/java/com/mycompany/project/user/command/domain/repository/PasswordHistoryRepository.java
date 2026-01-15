package com.mycompany.project.user.command.domain.repository;
import com.mycompany.project.user.command.domain.aggregate.PasswordHistory;
import com.mycompany.project.user.command.domain.aggregate.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    // 유저의 가장 최근 비밀번호 3개를 최신순으로 조회
    List<PasswordHistory> findTop3ByUserOrderByCreatedAtDesc(User user);
}