package com.asmj.marketplace.revenue.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("marketplace_commission_rules")
public class CommissionRule {
    @Id private String id;
    private String category;
    private boolean enabled;
    private String feeType; // PERCENTAGE, FIXED, BOTH
    private double percentage;
    private double fixedAmount;
    private String feePayer; // SUPPLIER, CUSTOMER, SPLIT
    private Instant updatedAt;
    private String updatedBy;
}
