package com.asmj.marketplace.payment.service;

import com.asmj.marketplace.payment.dto.PaymentDtos.*;
import com.asmj.marketplace.payment.model.*;
import com.asmj.marketplace.payment.repository.*;
import com.asmj.marketplace.revenue.service.RevenueService;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.vendor.repository.VendorRepository;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.vendor.model.Vendor;
import org.springframework.data.mongodb.core.query.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor
public class PaymentService {
 private final PaymentTransactionRepository payments; private final InvoiceRepository invoices; private final MongoTemplate mongo; private final RevenueService revenueService; private final UserRepository users; private final VendorRepository vendors;
 private String tx(){return "PAY-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();}
 private String inv(){return "INV-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();}
 private String date(String d){ if(d==null||d.isBlank()) return LocalDate.now().toString(); try{return LocalDate.parse(d).toString();}catch(Exception e){throw new IllegalArgumentException("Payment date must be YYYY-MM-DD");}}

 public record PaymentReferenceOption(String bookingId, String bookingType, String displayId, String status, Double amount, String customerName, String vendorName) {}

 public List<PaymentReferenceOption> referencesFor(String actorId, boolean vendorActor, boolean adminActor) {
   User actor = users.findByEmailIgnoreCase(actorId).orElseThrow(() -> new IllegalArgumentException("Signed-in user could not be resolved"));
   String userId = actor.getId();
   String vendorId = null;
   if (vendorActor && !adminActor) vendorId = vendors.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Vendor profile not found")).getId();
   String[][] sources = {
     {"PRODUCT", "product_orders"}, {"SERVICE", "service_bookings"}, {"RENTAL", "rental_bookings"},
     {"EVENT", "event_registrations"}, {"TUTOR", "bookings"}, {"JOB", "job_applications"},
     {"INSURANCE", "insurance_quote_requests"}, {"PRINTOUT", "printout_requests"}
   };
   List<PaymentReferenceOption> out = new ArrayList<>();
   Set<String> seen = new HashSet<>();
   for (String[] source : sources) {
     String type = source[0], collection = source[1];
     List<Document> docs;
     try { docs = mongo.findAll(Document.class, collection); } catch (Exception ignored) { continue; }
     for (Document d : docs) {
       String customer = first(d, "customerId", "userId", "customer_id", "user_id", "candidateId");
       String vendor = first(d, "vendorId", "providerId", "sellerId");
       if (!adminActor) {
         if (customer == null || vendor == null) continue;
         if (vendorActor ? !Objects.equals(vendorId, vendor) : !Objects.equals(userId, customer)) continue;
       }
       String id = first(d, "_id");
       String display = switch (type) {
         case "PRODUCT" -> first(d, "orderNumber", "_id");
         case "SERVICE", "RENTAL" -> first(d, "bookingNumber", "_id");
         case "EVENT" -> first(d, "registrationNumber", "_id");
         case "INSURANCE" -> first(d, "quoteNumber", "_id");
         case "PRINTOUT" -> first(d, "requestNumber", "_id");
         default -> first(d, "bookingNumber", "_id");
       };
       if (id == null || display == null) continue;
       String key = type + "|" + id;
       if (!seen.add(key)) continue;
       String status = first(d, "status", "paymentStatus");
       Double amount = numeric(d, "total", "totalAmount", "finalFare", "estimatedFare", "amount", "totalAmount", "estimatedPremium");
       String customerName = first(d, "customerName", "attendeeName", "contactName");
       String vendorName = first(d, "vendorName", "businessName", "sellerName");
       out.add(new PaymentReferenceOption(id, type, display, status, amount, customerName, vendorName));
     }
   }
   out.sort(Comparator.comparing(PaymentReferenceOption::bookingType).thenComparing(PaymentReferenceOption::displayId, String.CASE_INSENSITIVE_ORDER));
   return out;
 }
 private Double numeric(Document d, String... keys) { for (String k : keys) { Object v=d.get(k); if (v instanceof Number n) return n.doubleValue(); if (v != null) try { return Double.parseDouble(v.toString()); } catch (Exception ignored) {} } return null; }

 public PaymentTransaction submit(String actorId, boolean vendorActor, PaymentRequest r){
   if(r.getBookingId()==null||r.getBookingId().isBlank()) throw new IllegalArgumentException("Booking ID is required");
   if(r.getBookingType()==null||r.getBookingType().isBlank()) throw new IllegalArgumentException("Booking type is required");
   if(r.getPaymentType()==null || !Set.of("PAYMENT","REFUND").contains(r.getPaymentType())) throw new IllegalArgumentException("Payment type must be PAYMENT or REFUND");
   if(r.getAmount()<=0) throw new IllegalArgumentException("Amount must be greater than zero");
   if(r.getPaymentMethod()==null||r.getPaymentMethod().isBlank()) throw new IllegalArgumentException("Payment method is required");
   if(r.getPaymentReference()==null||r.getPaymentReference().isBlank()) throw new IllegalArgumentException("Payment reference is required");
   if(r.getScreenshotUrl()==null||r.getScreenshotUrl().isBlank()) throw new IllegalArgumentException("Payment screenshot is required");
   Reference ref = resolveReference(r.getBookingType(), r.getBookingId());
   if(ref==null) throw new IllegalArgumentException("Booking/transaction reference not found for the selected booking type. Check the reference number and selected type.");
   User user = users.findByEmailIgnoreCase(actorId).orElseThrow(() -> new IllegalArgumentException("Signed-in user could not be resolved"));
   String userId = user.getId();
   if(vendorActor) {
     Vendor vendor = vendors.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Vendor profile not found"));
     if(!Objects.equals(vendor.getId(), ref.vendorId())) throw new IllegalArgumentException("You are not the vendor for this booking");
   } else if(!Objects.equals(userId, ref.customerId())) {
     throw new IllegalArgumentException("You are not the customer for this booking");
   }
   PaymentTransaction p=PaymentTransaction.builder().transactionNumber(tx()).bookingId(r.getBookingId()).bookingType(r.getBookingType().toUpperCase(Locale.ROOT)).orderId(r.getOrderId())
     .payerId(ref.customerId()).vendorId(ref.vendorId()).customerName(r.getCustomerName()).customerMobile(r.getCustomerMobile()).paymentType(r.getPaymentType()).amount(r.getAmount())
     .currency(r.getCurrency()==null||r.getCurrency().isBlank()?"INR":r.getCurrency()).paymentMethod(r.getPaymentMethod()).paymentReference(r.getPaymentReference())
     .paymentDate(new PaymentTransaction.LocalDateOnly(LocalDate.parse(date(r.getPaymentDate())).getYear(),LocalDate.parse(date(r.getPaymentDate())).getMonthValue(),LocalDate.parse(date(r.getPaymentDate())).getDayOfMonth()))
     .screenshotUrl(r.getScreenshotUrl()).screenshotPublicId(r.getScreenshotPublicId()).notes(r.getNotes()).status("PENDING").createdAt(Instant.now()).updatedAt(Instant.now()).build();
   p = payments.save(p);
   syncBookingPaymentState(ref, p, "PENDING_REVIEW");
   return p;
 }
 
 private record Reference(String customerId,String vendorId,String collection,String documentId) {}
 private Reference resolveReference(String type,String id){
   String t=type.trim().toUpperCase(Locale.ROOT);
   String collection=switch(t){case "PRODUCT","ORDER"->"product_orders";case "SERVICE"->"service_bookings";case "RENTAL"->"rental_bookings";case "EVENT"->"event_registrations";case "TUTOR"->"bookings";case "JOB"->"job_applications";case "INSURANCE"->"insurance_quote_requests";case "PRINTOUT"->"printout_requests";default->"bookings";};
   Query q=new Query(new Criteria().orOperator(Criteria.where("_id").is(id),Criteria.where("bookingNumber").is(id),Criteria.where("orderNumber").is(id),Criteria.where("registrationNumber").is(id),Criteria.where("quoteNumber").is(id),Criteria.where("requestNumber").is(id)));
   Document d=mongo.findOne(q,Document.class,collection); if(d==null)return null;
   String customer=first(d,"customerId","userId","customer_id","user_id","candidateId"); String vendor=first(d,"vendorId");
   if(customer==null || vendor==null) return null;
   return new Reference(customer,vendor,collection,first(d,"_id"));
 }
 private void syncBookingPaymentState(Reference ref, PaymentTransaction p, String state) {
   if(ref==null || ref.collection()==null || ref.documentId()==null) return;
   Update update = new Update().set("paymentStatus", state).set("paymentReference", p.getPaymentReference())
       .set("paymentProofUrl", p.getScreenshotUrl()).set("paymentProofPublicId", p.getScreenshotPublicId()).set("paymentTransactionId", p.getId()).set("updatedAt", Instant.now());
   mongo.updateFirst(new Query(Criteria.where("_id").is(ref.documentId())), update, ref.collection());
 }
 private String first(Document d,String... keys){for(String k:keys){Object v=d.get(k);if(v!=null&&!v.toString().isBlank())return v.toString();}return null;}
 public List<PaymentTransaction> mine(String id){return payments.findByPayerIdOrderByCreatedAtDesc(id);}
 public List<PaymentTransaction> vendor(String id){return payments.findByVendorIdOrderByCreatedAtDesc(id);}
 public List<PaymentTransaction> byBooking(String id){return payments.findByBookingIdOrderByCreatedAtDesc(id);}
 public List<PaymentTransaction> all(){return payments.findAll().stream().sorted(Comparator.comparing(PaymentTransaction::getCreatedAt,Comparator.nullsLast(Comparator.reverseOrder()))).toList();}
 public PaymentTransaction review(String adminId,String id,ReviewRequest r){
   PaymentTransaction p=payments.findById(id).orElseThrow(()->new IllegalArgumentException("Payment not found"));
   if(r==null || r.getStatus()==null || !Set.of("VERIFIED","REJECTED").contains(r.getStatus())) throw new IllegalArgumentException("Status must be VERIFIED or REJECTED");
   if(!"PENDING".equals(p.getStatus())) throw new IllegalArgumentException("Only pending payment proofs can be reviewed. Submit a new proof if a rejected payment needs correction.");
   p.setStatus(r.getStatus());p.setVerifiedBy(adminId);p.setVerifiedAt(Instant.now());p.setRejectionReason(r.getRejectionReason());p.setUpdatedAt(Instant.now());
   p=payments.save(p);
   Reference ref = resolveReference(p.getBookingType(), p.getBookingId());
   if (ref != null) {
     String state = "REJECTED".equals(p.getStatus()) ? "REJECTED" : ("REFUND".equals(p.getPaymentType()) ? "REFUNDED" : "PAID");
     syncBookingPaymentState(ref, p, state);
   }
   if("VERIFIED".equals(p.getStatus())) { createInvoice(p); revenueService.recordVerifiedPayment(p); }
   return p;
 }
 private Invoice createInvoice(PaymentTransaction p){
   return invoices.findByTransactionId(p.getId()).orElseGet(()->invoices.save(Invoice.builder().invoiceNumber(inv()).transactionId(p.getId()).transactionNumber(p.getTransactionNumber())
     .bookingId(p.getBookingId()).bookingType(p.getBookingType()).invoiceType("REFUND".equals(p.getPaymentType())?"REFUND_NOTE":"INVOICE")
     .customerId(p.getPayerId()).vendorId(p.getVendorId()).customerName(p.getCustomerName()).amount(p.getAmount()).currency(p.getCurrency())
     .description(("REFUND".equals(p.getPaymentType())?"Refund":"Payment")+" against "+p.getBookingType()+" "+p.getBookingId())
     .paymentMethod(p.getPaymentMethod()).paymentReference(p.getPaymentReference()).invoiceDate(Instant.now()).status("ISSUED").createdAt(Instant.now()).build()));
 }
 public List<Invoice> invoicesForUser(String id){return invoices.findByCustomerIdOrderByInvoiceDateDesc(id);}
 public List<Invoice> invoicesForVendor(String id){return invoices.findByVendorIdOrderByInvoiceDateDesc(id);}
 public List<Invoice> allInvoices(){return invoices.findAll().stream().sorted(Comparator.comparing(Invoice::getInvoiceDate,Comparator.nullsLast(Comparator.reverseOrder()))).toList();}
}
