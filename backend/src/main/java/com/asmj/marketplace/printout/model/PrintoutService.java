package com.asmj.marketplace.printout.model;
import lombok.*; import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.mapping.Document; import java.math.BigDecimal; import java.time.Instant;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("printout_services")
public class PrintoutService { @Id private String id; private String vendorId, serviceName, description; private boolean pickupAvailable, homeDeliveryAvailable, active; private BigDecimal minimumHomeDeliveryAmount, homeDeliveryChargePercent, bwSingleRate, bwDoubleRate, colorSingleRate, colorDoubleRate, a3AdditionalCharge, bindingCharge, laminationCharge; private Instant createdAt,updatedAt; }
