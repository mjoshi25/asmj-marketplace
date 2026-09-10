package com.asmj.marketplace.payment.repository;
import com.asmj.marketplace.payment.model.PaymentTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction,String> {
    List<PaymentTransaction> findByPayerIdOrderByCreatedAtDesc(String payerId);
    List<PaymentTransaction> findByVendorIdOrderByCreatedAtDesc(String vendorId);
    List<PaymentTransaction> findByBookingIdOrderByCreatedAtDesc(String bookingId);
}
