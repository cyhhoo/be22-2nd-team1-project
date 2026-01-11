package com.mycompany.project.user.command.domain.repository;
import com.mycompany.project.user.command.domain.aggregate.AdminDetail;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AdminDetailRepository extends JpaRepository<AdminDetail, Long> {
}