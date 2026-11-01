package com.asmj.marketplace.rental.vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface VehicleRepository extends MongoRepository<Vehicle,String>{
 List<Vehicle> findByVendorIdOrderByCreatedAtDesc(String vendorId);
 List<Vehicle> findByStatusAndVehicleType(Vehicle.Status status,String vehicleType);
 List<Vehicle> findByVehicleTypeIgnoreCaseAndStatusIn(String vehicleType,Collection<Vehicle.Status> statuses);
 List<Vehicle> findByStatusOrderByCreatedAtDesc(Vehicle.Status status);
 List<Vehicle> findAllByOrderByCreatedAtDesc();
 boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);
}
