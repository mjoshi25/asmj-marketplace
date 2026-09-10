package com.asmj.marketplace.rental.fare;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("rental_fare_rules")
public class FareRule {
 @Id private String id;
 private String name,vehicleType,routeId,journeyType;
 private double baseFare,includedKm,extraKmRate,driverAllowance,nightCharge;
 @Builder.Default private ChargeMode tollMode=ChargeMode.ACTUAL;
 @Builder.Default private ChargeMode parkingMode=ChargeMode.ACTUAL;
 private double taxRate;
 @Builder.Default private boolean active=true;
 private Instant validFrom,validTo;
 private int priority;
 public enum ChargeMode{ACTUAL,INCLUDED,NONE}
}
