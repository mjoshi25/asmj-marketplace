package com.asmj.marketplace.event.model;
import lombok.*;import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.mapping.Document;import java.time.*;import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("event_listings")
public class EventListing { @Id String id; String vendorId; String organizerName; String name; String category; String description; LocalDate eventDate; LocalTime startTime; LocalTime endTime; String venue; String address; String city; int capacity; int availableSeats; double ticketPrice; LocalDateTime registrationDeadline; List<String> images; String terms; String status; boolean approved; boolean active; int views; Instant createdAt; Instant updatedAt; }
