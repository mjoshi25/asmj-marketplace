package com.asmj.marketplace.booking.service;

import com.asmj.marketplace.booking.model.Booking;
import com.asmj.marketplace.booking.repository.BookingRepository;
import com.asmj.marketplace.common.model.StatusHistory;
import com.asmj.marketplace.notification.service.NotificationService;
import com.asmj.marketplace.post.model.Post;
import com.asmj.marketplace.post.repository.PostRepository;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookings; private final PostRepository posts; private final UserRepository users; private final VendorRepository vendors; private final NotificationService notifications;

    public Booking create(String email, CreateRequest r) {
        if(r==null || r.listingId()==null || r.listingId().isBlank()) throw new IllegalArgumentException("Listing is required");
        var user=users.findByEmailIgnoreCase(email).orElseThrow(()->new IllegalArgumentException("User not found"));
        Post post=posts.findById(r.listingId()).orElseThrow(()->new IllegalArgumentException("Listing not found"));
        if(post.getStatus()!=Post.PostStatus.PUBLISHED) throw new IllegalArgumentException("This listing is not available");
        int qty=r.quantity()==null||r.quantity()<1?1:r.quantity();
        String type=r.bookingType()==null||r.bookingType().isBlank()?defaultType(post.getType()):r.bookingType().toUpperCase();
        validate(post.getType(),r,type);
        String initial=initialStatus(post.getType(),type);
        Instant now=Instant.now();
        Booking saved=bookings.save(Booking.builder().bookingType(type).listingId(post.getId()).userId(user.getId()).vendorId(post.getVendorId())
          .bookingDate(r.bookingDate()).startTime(r.startTime()).endTime(r.endTime()).quantity(qty).amount(r.amount()).status(initial)
          .notes(r.notes()).contactName(blankTo(user.getName(),r.contactName())).contactNumber(r.contactNumber()).address(r.address())
          .details(r.details()==null?new HashMap<>():r.details()).createdAt(now).updatedAt(now)
          .statusHistory(new ArrayList<>(List.of(history(initial,user,"Request created",now)))).build());
        vendors.findById(post.getVendorId()).ifPresent(v -> notifications.create(v.getUserId(), "New customer request", user.getName()+" submitted a "+pretty(type)+" for "+post.getTitle(), "BOOKING"));
        return enrichOne(saved);
    }

    private void validate(String listingType,CreateRequest r,String bookingType){
        String t=listingType==null?"":listingType.toUpperCase();
        if(r.contactNumber()==null||r.contactNumber().isBlank()) throw new IllegalArgumentException("Contact number is required");
        if(Set.of("SERVICE","TUTOR").contains(t) && (r.bookingDate()==null||r.startTime()==null)) throw new IllegalArgumentException("Date and start time are required");
        if("SERVICE".equals(t) && (r.address()==null||r.address().isBlank())) throw new IllegalArgumentException("Service address is required");
        if("EVENT".equals(t) && r.quantity()!=null && r.quantity()<1) throw new IllegalArgumentException("Attendee count must be at least 1");
        if("TUTOR".equals(t) && (r.details()==null||r.details().get("studentName")==null)) throw new IllegalArgumentException("Student name is required");
        if(Set.of("RENTAL","PROPERTY","VEHICLE_RENTAL").contains(t) && (r.details()==null||r.details().get("pickupDate")==null)) throw new IllegalArgumentException("Visit/start date is required");
        if("PRODUCT".equals(t) && (r.address()==null||r.address().isBlank())) throw new IllegalArgumentException("Delivery address is required");
    }

    public List<Booking> mine(String email){return enrich(bookings.findByUserIdOrderByCreatedAtDesc(users.findByEmailIgnoreCase(email).orElseThrow().getId()));}
    public List<Booking> vendor(String email){var user=users.findByEmailIgnoreCase(email).orElseThrow();var vendor=vendors.findByUserId(user.getId()).orElseThrow(()->new IllegalArgumentException("Vendor profile not found"));return enrich(bookings.findByVendorIdOrderByCreatedAtDesc(vendor.getId()));}
    public Booking one(String email,String id){var user=users.findByEmailIgnoreCase(email).orElseThrow();Booking b=bookings.findById(id).orElseThrow(()->new IllegalArgumentException("Booking not found"));var vendor=vendors.findByUserId(user.getId()).orElse(null);if(!Objects.equals(b.getUserId(),user.getId()) && (vendor==null||!Objects.equals(b.getVendorId(),vendor.getId())) && !user.getRoles().contains(com.asmj.marketplace.user.model.User.Role.ADMIN)) throw new IllegalArgumentException("You cannot view this booking");return enrichOne(b);}

    private List<Booking> enrich(List<Booking> list){for(Booking b:list){if(b.getStatusHistory()==null)b.setStatusHistory(new ArrayList<>());enrichOne(b);} return list;}
    private Booking enrichOne(Booking b){
        if(b.getStatusHistory()==null)b.setStatusHistory(new ArrayList<>());
        posts.findById(b.getListingId()).ifPresent(p->{b.setListingTitle(p.getTitle());b.setListingType(p.getType());b.setListingImage(p.getImages()!=null&&!p.getImages().isEmpty()?p.getImages().get(0):null);});
        users.findById(b.getUserId()).ifPresent(u->{b.setCustomerName(u.getName());b.setCustomerEmail(u.getEmail());b.setCustomerMobile(u.getMobile());}); return b;
    }

    public Booking updateVendorStatus(String email,String id,String status,String note){
        var user=users.findByEmailIgnoreCase(email).orElseThrow(); Booking b=bookings.findById(id).orElseThrow(()->new IllegalArgumentException("Booking not found"));
        var vendor=vendors.findByUserId(user.getId()).orElseThrow(()->new IllegalArgumentException("Vendor profile not found"));
        if(!Objects.equals(b.getVendorId(),vendor.getId()))throw new IllegalArgumentException("You cannot update this booking");
        String next=status==null?b.getStatus():status.toUpperCase(); String type=listingType(b);
        if(!allowed(type,b.getBookingType(),b.getStatus(),next)) throw new IllegalArgumentException("Cannot change "+pretty(b.getBookingType())+" from "+b.getStatus()+" to "+next);
        change(b,next,user,note==null?"Status updated":note); Booking saved=bookings.save(b);
        posts.findById(b.getListingId()).ifPresent(p->notifications.create(b.getUserId(), "Request updated", "Your "+pretty(b.getBookingType())+" for "+p.getTitle()+" is now "+next.toLowerCase().replace('_',' ')+".", "BOOKING_STATUS"));
        return enrichOne(saved);
    }

    public Booking cancelMine(String email,String id){
        var user=users.findByEmailIgnoreCase(email).orElseThrow(); Booking b=bookings.findById(id).orElseThrow(()->new IllegalArgumentException("Booking not found"));
        if(!Objects.equals(b.getUserId(),user.getId())) throw new IllegalArgumentException("You cannot cancel this request");
        if(!Set.of("PENDING","REGISTERED","ORDER_PLACED","REQUESTED").contains(b.getStatus())) throw new IllegalArgumentException("This request can no longer be cancelled");
        change(b,"CANCELLED",user,"Cancelled by customer"); Booking saved=bookings.save(b);
        vendors.findById(b.getVendorId()).ifPresent(v->posts.findById(b.getListingId()).ifPresent(p->notifications.create(v.getUserId(),"Customer cancelled request",user.getName()+" cancelled the "+pretty(b.getBookingType())+" for "+p.getTitle()+".","BOOKING_STATUS")));
        return enrichOne(saved);
    }

    private boolean allowed(String lt,String bt,String c,String n){
        if(c==null)return false; if(c.equals(n))return true;
        String t=lt.toUpperCase();
        if(Set.of("PRODUCT").contains(t)||"ORDER".equalsIgnoreCase(bt)) return c.equals("PENDING")||c.equals("ORDER_PLACED") ? Set.of("CONFIRMED","CANCELLED","REJECTED").contains(n) : c.equals("CONFIRMED")?Set.of("PROCESSING","CANCELLED").contains(n):c.equals("PROCESSING")&&n.equals("SHIPPED")||c.equals("SHIPPED")&&n.equals("DELIVERED");
        if("EVENT".equals(t)||"REGISTRATION".equalsIgnoreCase(bt)) return c.equals("PENDING")||c.equals("REGISTERED") ? Set.of("CONFIRMED","CANCELLED","REJECTED").contains(n) : c.equals("CONFIRMED")&&n.equals("ATTENDED");
        if("PROPERTY".equals(t)) return c.equals("PENDING")?Set.of("VISIT_SCHEDULED","REJECTED","CANCELLED").contains(n):c.equals("VISIT_SCHEDULED")?Set.of("NEGOTIATION","COMPLETED","CANCELLED").contains(n):c.equals("NEGOTIATION")&&n.equals("COMPLETED");
        if(Set.of("RENTAL","VEHICLE_RENTAL").contains(t)||"RESERVATION".equalsIgnoreCase(bt)) return c.equals("PENDING")?Set.of("CONFIRMED","REJECTED","CANCELLED").contains(n):c.equals("CONFIRMED")?Set.of("SCHEDULED","PICKUP","CANCELLED").contains(n):(c.equals("SCHEDULED")||c.equals("PICKUP"))?Set.of("RETURNED","COMPLETED").contains(n):false;
        return c.equals("PENDING")?Set.of("CONFIRMED","REJECTED","CANCELLED").contains(n):c.equals("CONFIRMED")?Set.of("SCHEDULED","COMPLETED","CANCELLED").contains(n):c.equals("SCHEDULED")&&n.equals("COMPLETED");
    }
    private String listingType(Booking b){return posts.findById(b.getListingId()).map(Post::getType).orElse("");}
    private void change(Booking b,String next,com.asmj.marketplace.user.model.User actor,String note){Instant now=Instant.now();b.setStatus(next);b.setUpdatedAt(now);if(b.getStatusHistory()==null)b.setStatusHistory(new ArrayList<>());b.getStatusHistory().add(history(next,actor,note,now));}
    private StatusHistory history(String status,com.asmj.marketplace.user.model.User actor,String note,Instant at){return StatusHistory.builder().status(status).actorId(actor.getId()).actorEmail(actor.getEmail()).note(note).changedAt(at).build();}
    private String initialStatus(String t,String bt){if("EVENT".equalsIgnoreCase(t))return "REGISTERED";if("PRODUCT".equalsIgnoreCase(t))return "ORDER_PLACED";return "PENDING";}
    private String defaultType(String t){return switch(t.toUpperCase()){case "PRODUCT"->"ORDER";case "SERVICE"->"BOOKING";case "TUTOR"->"SESSION";case "RENTAL","PROPERTY","VEHICLE_RENTAL"->"RESERVATION";case "EVENT"->"REGISTRATION";default->"BOOKING";};}
    private String pretty(String s){return s==null?"request":s.toLowerCase().replace('_',' ');} private String blankTo(String fallback,String value){return value==null||value.isBlank()?fallback:value;}
    public record CreateRequest(String listingId,String bookingType,LocalDate bookingDate,LocalTime startTime,LocalTime endTime,Integer quantity,Double amount,String notes,String contactName,String contactNumber,String address,Map<String,Object> details){}
}
