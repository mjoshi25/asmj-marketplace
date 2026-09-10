package com.asmj.marketplace.rental.booking;
import java.time.*;
import java.util.*;
import com.asmj.marketplace.rental.fare.*;
import com.asmj.marketplace.rental.location.*;
import com.asmj.marketplace.rental.route.*;
import com.asmj.marketplace.rental.vehicle.*;
public final class RentalDtos { private RentalDtos(){}
 public record QuoteRequest(String fromLocationId,String toLocationId,String vehicleType,RentalBooking.JourneyType journeyType,LocalDate travelDate,LocalTime pickupTime,Integer passengerCount,String discountCode,Double estimatedKm){}
 public record BookingRequest(String fromLocationId,String toLocationId,String vehicleType,RentalBooking.JourneyType journeyType,String pickupAddress,String pickupLandmark,String dropAddress,String dropLandmark,LocalDate travelDate,LocalTime pickupTime,LocalDate returnDate,LocalTime returnTime,Integer passengerCount,Integer luggageCount,String discountCode,String notes,String vehicleId,String driverId){}
 public record KmRequest(double km,Double lat,Double lng,Double toll,Double parking,Double otherCharges,String note){}
 public record StatusRequest(String status,String note){}
 public record VehicleRequest(Vehicle vehicle){}
 public record DriverRequest(String name,String mobile,String email,String password,String licenseNumber,String licenseType,LocalDate licenseExpiry,String assignedVehicleId,List<String> documents){}
 public record LocationRequest(LocationMaster location){}
 public record RouteRequest(RentalRoute route){}
 public record FareRuleRequest(FareRule rule){}
 public record DiscountRuleRequest(DiscountRule rule){}
}
