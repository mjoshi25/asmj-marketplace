package com.asmj.marketplace.rental.vehicle;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.*;
import java.util.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("rental_drivers")
public class Driver {
 @Id private String id;
 @Indexed(unique=true) private String userId;
 @Indexed private String vendorId;
 private String name,mobile,licenseNumber,licenseType;
 private LocalDate licenseExpiry;
 private String assignedVehicleId;
 @Builder.Default private Status status=Status.ACTIVE;
 @Builder.Default private List<String> documents=new ArrayList<>();
 private Instant createdAt,updatedAt;
 public enum Status{ACTIVE,INACTIVE,SUSPENDED}
}
