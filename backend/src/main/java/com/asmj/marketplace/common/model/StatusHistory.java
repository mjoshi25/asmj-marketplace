package com.asmj.marketplace.common.model;

import lombok.*;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StatusHistory {
    private String status;
    private String actorId;
    private String actorEmail;
    private String note;
    private Instant changedAt;
}
