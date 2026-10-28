package com.asmj.marketplace.localpromotion.repository;

import com.asmj.marketplace.localpromotion.model.LocalPromotion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.Instant;
import java.util.List;

public interface LocalPromotionRepository extends MongoRepository<LocalPromotion, String> {
    List<LocalPromotion> findByVendorIdOrderBySubmittedAtDesc(String vendorId);
    List<LocalPromotion> findByStatusOrderBySubmittedAtDesc(LocalPromotion.Status status);
    List<LocalPromotion> findByStatusAndEndsAtAfterOrderByStartsAtDesc(LocalPromotion.Status status, Instant now);
    List<LocalPromotion> findByStatusAndEndsAtBefore(LocalPromotion.Status status, Instant now);
}
