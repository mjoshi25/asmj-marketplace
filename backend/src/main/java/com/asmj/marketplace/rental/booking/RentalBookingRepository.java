package com.asmj.marketplace.rental.booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface RentalBookingRepository extends MongoRepository<RentalBooking,String>{
 List<RentalBooking> findByCustomerIdOrderByCreatedAtDesc(String id);
 List<RentalBooking> findByVendorIdOrderByCreatedAtDesc(String id);
 List<RentalBooking> findByDriverIdOrderByTravelDateAscPickupTimeAsc(String id);
 boolean existsByVehicleIdAndStatusIn(String vehicleId,Collection<RentalBooking.Status> statuses);
 boolean existsByDriverIdAndStatusIn(String driverId,Collection<RentalBooking.Status> statuses);
}
