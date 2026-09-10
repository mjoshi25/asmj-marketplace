package com.asmj.marketplace.rental.audit;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("rental_audit_logs")
public class RentalAudit {
 @Id private String id;
 @Indexed private String entityType;
 @Indexed private String entityId;
 private String action,actorId,actorEmail,role,oldValue,newValue,reason;
 private Instant createdAt;
}
