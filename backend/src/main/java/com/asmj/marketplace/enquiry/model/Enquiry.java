package com.asmj.marketplace.enquiry.model;
import lombok.*;import org.springframework.data.annotation.*;import org.springframework.data.mongodb.core.mapping.Document;import java.time.Instant;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("enquiries")
public class Enquiry{@Id String id;String postId,userId,vendorId,type,message,status;Instant createdAt,updatedAt;@Transient String listingTitle;@Transient String listingType;@Transient String listingImage;@Transient String customerName;@Transient String customerEmail;@Transient String customerMobile;}
