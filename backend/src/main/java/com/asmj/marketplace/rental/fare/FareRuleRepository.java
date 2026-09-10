package com.asmj.marketplace.rental.fare;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface FareRuleRepository extends MongoRepository<FareRule,String>{List<FareRule> findByActiveTrueOrderByPriorityDesc();}
