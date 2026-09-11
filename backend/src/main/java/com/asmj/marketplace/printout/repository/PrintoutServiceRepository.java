package com.asmj.marketplace.printout.repository;
import com.asmj.marketplace.printout.model.PrintoutService; import org.springframework.data.mongodb.repository.MongoRepository; import java.util.*;
public interface PrintoutServiceRepository extends MongoRepository<PrintoutService,String>{ List<PrintoutService> findByActiveTrueOrderByCreatedAtDesc(); List<PrintoutService> findByVendorIdOrderByCreatedAtDesc(String vendorId); }
