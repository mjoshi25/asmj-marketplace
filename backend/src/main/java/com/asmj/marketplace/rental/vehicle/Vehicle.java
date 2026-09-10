package com.asmj.marketplace.rental.vehicle;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.Instant;
import java.util.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("rental_vehicles")
public class Vehicle {
 @Id private String id;
 @Indexed private String vendorId;
 @Indexed(unique=true) private String registrationNumber;
 @Indexed private String vehicleType;
 private String make,model;
 private Integer year,seats;
 private String fuelType,transmission;
 private boolean ac;
 private double currentKm;
 @Builder.Default private Status status=Status.AVAILABLE;
 private String insuranceNumber;
 private LocalDate insuranceExpiry;
 private String permitNumber,permitType,permitIssuingState;
 private LocalDate permitExpiry;
 @Builder.Default private Set<String> coveredStates=new HashSet<>();
 @Builder.Default private List<String> documents=new ArrayList<>();
 private Instant createdAt,updatedAt;
 public enum Status{AVAILABLE,BOOKED,ON_TRIP,MAINTENANCE,INACTIVE}
}
