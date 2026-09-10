package com.asmj.marketplace.rental.route;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("rental_routes")
public class RentalRoute {
 @Id private String id;
 @Indexed private String fromLocationId;
 @Indexed private String toLocationId;
 private String fromStateCode,fromState,fromCity,toStateCode,toState,toCity;
 private boolean interstate;
 @Builder.Default private boolean oneWayAllowed=true;
 @Builder.Default private boolean roundTripAllowed=true;
 private double estimatedKm,minKm,maxKm;
 private long estimatedMinutes;
 @Builder.Default private boolean active=true;
 private Instant createdAt,updatedAt;
}
