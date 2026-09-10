package com.asmj.marketplace.event.model;
import lombok.*;import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.mapping.Document;import java.time.*;import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("event_registrations")
public class EventRegistration { @Id String id; String registrationNumber; String eventId; String vendorId; String userId; String attendeeName; String attendeeMobile; int quantity; double unitPrice; double totalAmount; String status; Instant registeredAt; Instant cancelledAt; List<StatusEntry> history; @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class StatusEntry { String status; String actorId; String note; Instant at; } }
