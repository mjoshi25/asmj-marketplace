package com.asmj.marketplace.booking.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.*;
import java.util.*;
import com.asmj.marketplace.common.model.StatusHistory;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("bookings")
public class Booking {
    @Id private String id;
    @Indexed private String bookingType;
    @Indexed private String listingId;
    @Indexed private String userId;
    @Indexed private String vendorId;
    private LocalDate bookingDate;
    private LocalTime startTime, endTime;
    private int quantity;
    private Double amount;
    private String status;
    @Builder.Default private List<StatusHistory> statusHistory = new ArrayList<>();
    private String notes;
    private String contactName;
    private String contactNumber;
    private String address;
    private Map<String,Object> details;
    private Instant createdAt, updatedAt;
    @Transient private String listingTitle;
    @Transient private String listingType;
    @Transient private String listingImage;
    @Transient private String customerName;
    @Transient private String customerEmail;
    @Transient private String customerMobile;
}
