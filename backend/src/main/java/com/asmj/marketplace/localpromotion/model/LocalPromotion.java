package com.asmj.marketplace.localpromotion.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("local_promotions")
public class LocalPromotion {
    @Id private String id;
    @Indexed private String vendorId;
    private String businessName;
    private String targetType;
    private String targetId;
    private String packageCode;
    private String packageName;
    private int durationDays;
    private double amountInr;
    private String paymentProofUrl;
    @Indexed private Status status;
    private String adminNote;
    private Instant submittedAt;
    private Instant verifiedAt;
    private Instant startsAt;
    @Indexed private Instant endsAt;
    private long views;
    private long clicks;
    public enum Status { PENDING_VERIFICATION, ACTIVE, EXPIRED, REJECTED }
}
