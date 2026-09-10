package com.asmj.marketplace.payment.service;

import com.asmj.marketplace.payment.dto.PaymentDtos.*;
import com.asmj.marketplace.payment.model.*;
import com.asmj.marketplace.payment.repository.*;
import com.asmj.marketplace.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service @RequiredArgsConstructor
public class PaymentService {
 private final PaymentTransactionRepository payments; private final InvoiceRepository invoices; private final MongoTemplate mongo;
 private String tx(){return "PAY-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();}
 private String inv(){return "INV-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();}
 private String date(String d){ if(d==null||d.isBlank()) return LocalDate.now().toString(); try{return LocalDate.parse(d).toString();}catch(Exception e){throw new IllegalArgumentException("Payment date must be YYYY-MM-DD");}}
 public PaymentTransaction submit(String actorId, boolean vendorActor, PaymentRequest r){
   if(r.getBookingId()==null||r.getBookingId().isBlank()) throw new IllegalArgumentException("Booking ID is required");
   if(r.getBookingType()==null||r.getBookingType().isBlank()) throw new IllegalArgumentException("Booking type is required");
   if(r.getPaymentType()==null || !Set.of("PAYMENT","REFUND").contains(r.getPaymentType())) throw new IllegalArgumentException("Payment type must be PAYMENT or REFUND");
   if(r.getAmount()<=0) throw new IllegalArgumentException("Amount must be greater than zero");
   if(r.getPaymentMethod()==null||r.getPaymentMethod().isBlank()) throw new IllegalArgumentException("Payment method is required");
   if(r.getPaymentReference()==null||r.getPaymentReference().isBlank()) throw new IllegalArgumentException("Payment reference is required");
   if(r.getScreenshotUrl()==null||r.getScreenshotUrl().isBlank()) throw new IllegalArgumentException("Payment screenshot is required");
   Reference ref = resolveReference(r.getBookingType(), r.getBookingId());
   if(ref==null) throw new IllegalArgumentException("Booking/transaction reference not found for the selected booking type");
   if(vendorActor && !actorId.equals(ref.vendorId())) throw new IllegalArgumentException("You are not the vendor for this booking");
   if(!vendorActor && !actorId.equals(ref.customerId())) throw new IllegalArgumentException("You are not the customer for this booking");
   PaymentTransaction p=PaymentTransaction.builder().transactionNumber(tx()).bookingId(r.getBookingId()).bookingType(r.getBookingType()).orderId(r.getOrderId())
     .payerId(vendorActor?ref.customerId():actorId).vendorId(ref.vendorId()).customerName(r.getCustomerName()).customerMobile(r.getCustomerMobile()).paymentType(r.getPaymentType()).amount(r.getAmount())
     .currency(r.getCurrency()==null||r.getCurrency().isBlank()?"INR":r.getCurrency()).paymentMethod(r.getPaymentMethod()).paymentReference(r.getPaymentReference())
     .paymentDate(new PaymentTransaction.LocalDateOnly(LocalDate.parse(date(r.getPaymentDate())).getYear(),LocalDate.parse(date(r.getPaymentDate())).getMonthValue(),LocalDate.parse(date(r.getPaymentDate())).getDayOfMonth()))
     .screenshotUrl(r.getScreenshotUrl()).screenshotPublicId(r.getScreenshotPublicId()).notes(r.getNotes()).status("PENDING").createdAt(Instant.now()).updatedAt(Instant.now()).build();
   return payments.save(p);
 }
 
 private record Reference(String customerId,String vendorId) {}
 private Reference resolveReference(String type,String id){
   String t=type.toUpperCase();
   String collection=switch(t){case "PRODUCT","ORDER"->"product_orders";case "SERVICE"->"service_bookings";case "RENTAL"->"rental_bookings";case "EVENT"->"event_registrations";case "TUTOR"->"tutor_bookings";case "JOB"->"job_applications";case "INSURANCE"->"insurance_quote_requests";default->"bookings";};
   Query q=new Query(new Criteria().orOperator(Criteria.where("_id").is(id),Criteria.where("bookingNumber").is(id),Criteria.where("orderNumber").is(id),Criteria.where("registrationNumber").is(id),Criteria.where("quoteNumber").is(id)));
   Document d=mongo.findOne(q,Document.class,collection); if(d==null)return null;
   String customer=first(d,"customerId","userId","customer_id","user_id","candidateId"); String vendor=first(d,"vendorId");
   return new Reference(customer,vendor);
 }
 private String first(Document d,String... keys){for(String k:keys){Object v=d.get(k);if(v!=null&&!v.toString().isBlank())return v.toString();}return null;}
 public List<PaymentTransaction> mine(String id){return payments.findByPayerIdOrderByCreatedAtDesc(id);}
 public List<PaymentTransaction> vendor(String id){return payments.findByVendorIdOrderByCreatedAtDesc(id);}
 public List<PaymentTransaction> byBooking(String id){return payments.findByBookingIdOrderByCreatedAtDesc(id);}
 public List<PaymentTransaction> all(){return payments.findAll().stream().sorted(Comparator.comparing(PaymentTransaction::getCreatedAt,Comparator.nullsLast(Comparator.reverseOrder()))).toList();}
 public PaymentTransaction review(String adminId,String id,ReviewRequest r){
   PaymentTransaction p=payments.findById(id).orElseThrow(()->new IllegalArgumentException("Payment not found"));
   if(!Set.of("VERIFIED","REJECTED").contains(r.getStatus())) throw new IllegalArgumentException("Status must be VERIFIED or REJECTED");
   p.setStatus(r.getStatus());p.setVerifiedBy(adminId);p.setVerifiedAt(Instant.now());p.setRejectionReason(r.getRejectionReason());p.setUpdatedAt(Instant.now());
   p=payments.save(p);
   if("VERIFIED".equals(p.getStatus())) createInvoice(p);
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
