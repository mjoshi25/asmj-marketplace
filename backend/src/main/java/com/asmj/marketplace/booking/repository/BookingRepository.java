package com.asmj.marketplace.booking.repository;

import com.asmj.marketplace.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;

public interface BookingRepository extends MongoRepository<Booking,String> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Booking> findByVendorIdOrderByCreatedAtDesc(String vendorId);
}
