package com.asmj.marketplace.payment.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("invoices")
public class Invoice {
    @Id private String id;
    @Indexed(unique=true) private String invoiceNumber;
    @Indexed private String transactionId;
    private String transactionNumber;
    @Indexed private String bookingId;
    private String bookingType;
    private String invoiceType; // INVOICE or REFUND_NOTE
    private String customerId;
    private String vendorId;
    private String customerName;
    private double amount;
    private String currency;
    private String description;
    private String paymentMethod;
    private String paymentReference;
    private Instant invoiceDate;
    private String status; // ISSUED, VOID
    private Instant createdAt;
}
