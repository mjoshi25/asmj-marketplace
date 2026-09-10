package com.asmj.marketplace.payment.repository;
import com.asmj.marketplace.payment.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface InvoiceRepository extends MongoRepository<Invoice,String> {
    Optional<Invoice> findByTransactionId(String transactionId);
    List<Invoice> findByCustomerIdOrderByInvoiceDateDesc(String customerId);
    List<Invoice> findByVendorIdOrderByInvoiceDateDesc(String vendorId);
}
