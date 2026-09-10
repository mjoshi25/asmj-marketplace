package com.asmj.marketplace.rental.booking;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.asmj.marketplace.common.model.StatusHistory;
import java.time.*;
import java.util.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("rental_bookings")
public class RentalBooking {
 @Id private String id;
 @Indexed(unique=true) private String bookingNumber;
 @Indexed private String customerId,vendorId,vehicleId,driverId,routeId;
 private String fromLocationId,toLocationId,fromStateCode,fromState,fromCity,toStateCode,toState,toCity;
 @Builder.Default private JourneyType journeyType=JourneyType.ONE_WAY;
 private String pickupAddress,pickupLandmark,dropAddress,dropLandmark;
 private LocalDate travelDate,returnDate;
 private LocalTime pickupTime,returnTime;
 private int passengerCount,luggageCount;
 private String customerName,customerMobile,customerEmail,notes;
 @Builder.Default private Status status=Status.REQUESTED;
 private double estimatedFare,finalFare;
 private Double startKm,outboundEndKm,returnStartKm,endKm,actualKm;
 private Instant startAt,outboundEndAt,returnStartAt,endAt;
 private Double startLat,startLng,endLat,endLng,toll,parking,otherCharges;
 private FareBreakdown fareBreakdown;
 private String discountRuleId,discountCode,cancellationReason;
 @Builder.Default private List<StatusHistory> statusHistory=new ArrayList<>();
 private Instant createdAt,updatedAt;
 public enum JourneyType{ONE_WAY,ROUND_TRIP}
 public enum Status{REQUESTED,VENDOR_CONFIRMED,DRIVER_ASSIGNED,CUSTOMER_CONFIRMED,DRIVER_EN_ROUTE,JOURNEY_STARTED,JOURNEY_IN_PROGRESS,OUTBOUND_COMPLETED,RETURN_STARTED,JOURNEY_COMPLETED,FARE_FINALIZED,PAYMENT_PENDING,COMPLETED,CANCELLED,REJECTED,NO_SHOW,DISPUTED}
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class FareBreakdown{private double baseFare,includedKm,actualKm,extraKm,extraKmCharge,driverAllowance,nightCharge,toll,parking,otherCharges,taxableAmount,tax,discount,gross,finalAmount;}
}
