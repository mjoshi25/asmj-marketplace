package com.asmj.marketplace.revenue.repository;
import com.asmj.marketplace.revenue.model.FeeLedgerEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;
public interface FeeLedgerRepository extends MongoRepository<FeeLedgerEntry,String> { List<FeeLedgerEntry> findAllByOrderByCreatedAtDesc(); Optional<FeeLedgerEntry> findByPaymentTransactionId(String paymentTransactionId); }
