package com.asmj.marketplace.rental.fare;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.*;
import java.util.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("rental_discount_rules")
public class DiscountRule {
 @Id private String id;
 @Builder.Default private String code="";
 private String name,type;
 private double value,minFare,maxDiscount;
 private Instant validFrom,validTo;
 @Builder.Default private Set<String> vehicleTypes=new HashSet<>();
 @Builder.Default private Set<String> routeIds=new HashSet<>();
 @Builder.Default private Set<String> journeyTypes=new HashSet<>();
 @Builder.Default private boolean active=true;
 public enum Type{PERCENTAGE,FIXED}
}
