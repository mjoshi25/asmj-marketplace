package com.asmj.marketplace.admin.repository;

import com.asmj.marketplace.admin.model.AdminAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdminAuditLogRepository extends MongoRepository<AdminAuditLog, String> {
    List<AdminAuditLog> findAllByOrderByCreatedAtDesc();
    List<AdminAuditLog> findByTargetIdOrderByCreatedAtDesc(String targetId);
}
