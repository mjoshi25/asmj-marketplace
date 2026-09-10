package com.asmj.marketplace.payment.dto;
import lombok.*;
public class PaymentDtos {
 @Data @NoArgsConstructor @AllArgsConstructor public static class PaymentRequest {
   private String bookingId; private String bookingType; private String orderId; private String paymentType;
   private double amount; private String currency; private String paymentMethod; private String paymentReference;
   private String paymentDate; private String screenshotUrl; private String screenshotPublicId; private String notes;
   private String customerName; private String customerMobile;
 }
 @Data @NoArgsConstructor @AllArgsConstructor public static class ReviewRequest { private String status; private String rejectionReason; }
}
