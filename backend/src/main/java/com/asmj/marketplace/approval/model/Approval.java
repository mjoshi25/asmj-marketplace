package com.asmj.marketplace.approval.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("approvals")
public class Approval {
    @Id private String id;
    private String targetId;
    private TargetType targetType;
    private Action action;
    private String adminEmail;
    private String comments;
    private Instant createdAt;
    public enum TargetType { POST, VENDOR }
    public enum Action { APPROVE, REJECT }
}
