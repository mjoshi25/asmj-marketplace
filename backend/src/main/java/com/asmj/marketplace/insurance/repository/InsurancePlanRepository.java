package com.asmj.marketplace.insurance.repository;
import com.asmj.marketplace.insurance.model.InsurancePlan;import org.springframework.data.mongodb.repository.MongoRepository;import java.util.*;
public interface InsurancePlanRepository extends MongoRepository<InsurancePlan,String>{ List<InsurancePlan> findByApprovedTrueAndActiveTrueOrderByFeaturedDescCreatedAtDesc(); List<InsurancePlan> findByVendorIdOrderByCreatedAtDesc(String vendorId); }
