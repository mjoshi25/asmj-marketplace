package com.asmj.marketplace.printout.repository;
import com.asmj.marketplace.printout.model.PrintoutRequest; import org.springframework.data.mongodb.repository.MongoRepository; import java.util.*;
public interface PrintoutRequestRepository extends MongoRepository<PrintoutRequest,String>{ List<PrintoutRequest> findByCustomerIdOrderByCreatedAtDesc(String id); List<PrintoutRequest> findByVendorIdOrderByCreatedAtDesc(String id); }
