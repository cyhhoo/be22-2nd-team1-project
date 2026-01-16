package com.mycompany.project.user.command.domain.repository;

import com.mycompany.project.user.command.domain.aggregate.PasswordHistory;
import com.mycompany.project.user.command.domain.aggregate.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    // Retrieve the 3 most recent passwords for the user
    List<PasswordHistory> findTop3ByUserOrderByCreatedAtDesc(User user);
}