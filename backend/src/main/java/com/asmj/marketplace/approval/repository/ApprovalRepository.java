package com.asmj.marketplace.approval.repository;
import com.asmj.marketplace.approval.model.Approval;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface ApprovalRepository extends MongoRepository<Approval,String>{
    List<Approval> findByTargetIdOrderByCreatedAtDesc(String targetId);
}
