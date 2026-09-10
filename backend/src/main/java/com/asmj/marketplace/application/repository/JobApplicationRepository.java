package com.asmj.marketplace.application.repository;

import com.asmj.marketplace.application.model.JobApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;

public interface JobApplicationRepository extends MongoRepository<JobApplication,String> {
    Optional<JobApplication> findByPostIdAndUserId(String postId, String userId);
    List<JobApplication> findByUserIdOrderByCreatedAtDesc(String userId);
    List<JobApplication> findByVendorIdOrderByCreatedAtDesc(String vendorId);
}
