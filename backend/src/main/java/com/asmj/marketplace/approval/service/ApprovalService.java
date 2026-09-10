package com.asmj.marketplace.approval.service;
import com.asmj.marketplace.approval.model.Approval;
import com.asmj.marketplace.approval.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
@Service @RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepository repo;
    public Approval record(String targetId, Approval.TargetType type, Approval.Action action, String adminEmail, String comments){
        return repo.save(Approval.builder().targetId(targetId).targetType(type).action(action).adminEmail(adminEmail).comments(comments).createdAt(Instant.now()).build());
    }
    public List<Approval> history(String targetId){return repo.findByTargetIdOrderByCreatedAtDesc(targetId);}
}
