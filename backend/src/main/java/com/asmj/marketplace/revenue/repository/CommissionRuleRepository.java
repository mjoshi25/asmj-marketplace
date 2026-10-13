package com.asmj.marketplace.revenue.repository;
import com.asmj.marketplace.revenue.model.CommissionRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface CommissionRuleRepository extends MongoRepository<CommissionRule,String> { Optional<CommissionRule> findByCategoryIgnoreCase(String category); }
