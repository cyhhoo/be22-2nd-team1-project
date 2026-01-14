package com.mycompany.project.common.repository;

import com.mycompany.project.common.entity.BulkUploadLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulkUploadLogRepository extends JpaRepository<BulkUploadLog, Long> {
}
