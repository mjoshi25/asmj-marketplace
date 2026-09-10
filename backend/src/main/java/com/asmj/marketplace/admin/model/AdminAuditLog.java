package com.asmj.marketplace.admin.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("admin_audit_logs")
public class AdminAuditLog {
    @Id private String id;
    private String actorEmail;
    private Action action;
    private TargetType targetType;
    private String targetId;
    private String details;
    private Instant createdAt;

    public enum Action {
        APPROVE_VENDOR, REJECT_VENDOR,
        APPROVE_POST, REJECT_POST,
        ACTIVATE_USER, DEACTIVATE_USER, SUSPEND_USER,
        ADD_ROLE, REMOVE_ROLE
    }

    public enum TargetType { USER, VENDOR, POST }
}
