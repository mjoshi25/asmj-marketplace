package com.asmj.marketplace.service.model;
import lombok.*;import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.index.Indexed;import org.springframework.data.mongodb.core.mapping.Document;import java.time.Instant;import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("service_listings") public class ServiceListing{
 @Id String id; @Indexed String vendorId; @Indexed String categoryId; @Indexed String name; String description,brand,serviceMode; boolean active,approved,featured; List<String> images; List<String> serviceAreas; List<Package> packages; int slotDurationMinutes,bufferMinutes,maxBookingsPerSlot; double taxRate,commissionRate; CancellationPolicy cancellationPolicy; Instant createdAt,updatedAt;
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class Package{String id,name,description;double price;int durationMinutes;boolean active;}
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class CancellationPolicy{int freeCancellationHours;double cancellationFeePercent;}
}
