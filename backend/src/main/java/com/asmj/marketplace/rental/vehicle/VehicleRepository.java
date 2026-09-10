package com.asmj.marketplace.rental.vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface VehicleRepository extends MongoRepository<Vehicle,String>{
 List<Vehicle> findByVendorIdOrderByCreatedAtDesc(String vendorId);
 List<Vehicle> findByStatusAndVehicleType(Vehicle.Status status,String vehicleType);
 List<Vehicle> findByStatusOrderByCreatedAtDesc(Vehicle.Status status);
 boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);
}
