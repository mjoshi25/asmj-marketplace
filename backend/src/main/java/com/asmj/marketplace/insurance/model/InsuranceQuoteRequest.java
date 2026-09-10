package com.asmj.marketplace.insurance.model;
import lombok.*;import org.springframework.data.annotation.*;import org.springframework.data.mongodb.core.mapping.Document;import java.time.*;import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("insurance_quote_requests")
public class InsuranceQuoteRequest { @Id String id; String quoteNumber; String planId; String vendorId; String userId; String customerName; String customerMobile; String customerEmail; Integer age; Integer members; String city; String occupation; String coverageNeed; double estimatedPremium; String status; String vendorNotes; List<StatusEntry> history; Instant createdAt; Instant updatedAt;
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class StatusEntry { String status; String actorId; String note; Instant at; }
}
