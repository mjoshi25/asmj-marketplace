package com.asmj.marketplace.payment.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("payment_transactions")
public class PaymentTransaction {
    @Id private String id;
    @Indexed(unique=true) private String transactionNumber;
    @Indexed private String bookingId;
    @Indexed private String bookingType;
    private String orderId;
    @Indexed private String payerId;
    @Indexed private String vendorId;
    private String customerName;
    private String customerMobile;
    private String paymentType; // PAYMENT or REFUND
    private double amount;
    private String currency;
    private String paymentMethod; // UPI, BANK_TRANSFER, CASH, CARD, OTHER
    private String paymentReference;
    private LocalDateOnly paymentDate;
    private String screenshotUrl;
    private String screenshotPublicId;
    private String notes;
    private String status; // PENDING, VERIFIED, REJECTED
    private String verifiedBy;
    private Instant verifiedAt;
    private String rejectionReason;
    private Instant createdAt;
    private Instant updatedAt;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LocalDateOnly { private int year; private int month; private int day; }

    @Transient private InvoiceSummary invoice;
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class InvoiceSummary { private String invoiceNumber; private String invoiceType; }
}
