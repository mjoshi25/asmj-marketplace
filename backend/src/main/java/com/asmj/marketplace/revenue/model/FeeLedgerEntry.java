package com.asmj.marketplace.revenue.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("marketplace_fee_ledger")
public class FeeLedgerEntry {
    @Id private String id;
    @Indexed(unique=true) private String paymentTransactionId;
    private String transactionNumber;
    private String bookingId;
    private String category;
    private String customerId;
    private String supplierId;
    private double transactionAmount;
    private double percentage;
    private double fixedAmount;
    private double platformFee;
    private double customerFee;
    private double supplierFee;
    private String feePayer;
    private String status; // CALCULATED, COLLECTED, WAIVED, REVERSED
    private Instant createdAt;
}
