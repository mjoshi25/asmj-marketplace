package com.asmj.marketplace.rental.location;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("rental_locations")
public class LocationMaster {
 @Id private String id;
 @Indexed private String stateCode;
 @Indexed private String stateName;
 @Indexed private String cityName;
 @Indexed private String pincode;
 @Indexed private String slug;
 @Builder.Default private boolean active=true;
 @Builder.Default private boolean originAllowed=true;
 @Builder.Default private boolean destinationAllowed=true;
 private boolean airport;
 private boolean railway;
 private boolean localPickup;
 private Instant createdAt, updatedAt;
}
