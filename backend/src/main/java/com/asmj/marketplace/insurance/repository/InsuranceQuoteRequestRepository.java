package com.asmj.marketplace.insurance.repository;
import com.asmj.marketplace.insurance.model.InsuranceQuoteRequest;import org.springframework.data.mongodb.repository.MongoRepository;import java.util.*;
public interface InsuranceQuoteRequestRepository extends MongoRepository<InsuranceQuoteRequest,String>{ List<InsuranceQuoteRequest> findByUserIdOrderByCreatedAtDesc(String userId); List<InsuranceQuoteRequest> findByVendorIdOrderByCreatedAtDesc(String vendorId); }
