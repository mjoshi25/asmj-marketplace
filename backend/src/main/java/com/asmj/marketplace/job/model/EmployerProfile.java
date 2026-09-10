package com.asmj.marketplace.job.model;
import lombok.*; import org.springframework.data.annotation.*; import org.springframework.data.mongodb.core.index.Indexed; import org.springframework.data.mongodb.core.mapping.Document; import java.time.Instant; import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("employer_profiles")
public class EmployerProfile { @Id private String id; @Indexed private String vendorId; private String companyName,legalName,website,description,industry,companySize,logoUrl,address,city,state,pincode; private String contactName,contactEmail,contactMobile; private boolean verified; private Instant createdAt,updatedAt; }
