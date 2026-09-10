package com.asmj.marketplace.rental.vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface DriverRepository extends MongoRepository<Driver,String>{
 Optional<Driver> findByUserId(String userId);
 List<Driver> findByVendorIdOrderByCreatedAtDesc(String vendorId);
}
