package com.asmj.marketplace.rental.fare;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface DiscountRuleRepository extends MongoRepository<DiscountRule,String>{Optional<DiscountRule> findByCodeIgnoreCase(String code);List<DiscountRule> findByActiveTrueOrderByNameAsc();}
