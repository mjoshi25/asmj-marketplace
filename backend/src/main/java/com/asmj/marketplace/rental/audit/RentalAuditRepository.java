package com.asmj.marketplace.rental.audit;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface RentalAuditRepository extends MongoRepository<RentalAudit,String>{List<RentalAudit> findByEntityTypeOrderByCreatedAtDesc(String type);List<RentalAudit> findByEntityIdOrderByCreatedAtDesc(String id);}
