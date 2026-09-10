package com.asmj.marketplace.product.model;
import lombok.*;import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.index.Indexed;import org.springframework.data.mongodb.core.mapping.Document;import java.time.*;import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("product_orders") public class Order{
 @Id private String id;@Indexed(unique=true) private String orderNumber;@Indexed private String customerId,vendorId;private List<Item> items;private Address shippingAddress;private double subtotal,tax,discount,shippingFee,total;private String couponCode,paymentStatus,paymentReference;@Builder.Default private Status status=Status.PLACED;private List<StatusEntry> statusHistory;private Instant createdAt,updatedAt;
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class Item{private String productId,variantId,name,variantName;private int quantity;private double unitPrice,taxAmount,total;}
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class Address{private String name,mobile,line1,line2,area,city,state,pincode,landmark;}
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class StatusEntry{private Status status;private String actorId,note;private Instant at;}
 public enum Status{PLACED,CONFIRMED,PROCESSING,PACKED,SHIPPED,OUT_FOR_DELIVERY,DELIVERED,CANCELLED,RETURN_REQUESTED,RETURN_APPROVED,RETURNED,REFUNDED}
}
