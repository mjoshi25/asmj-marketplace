package com.asmj.marketplace.admin.service;

import com.asmj.marketplace.admin.model.AdminAuditLog;
import com.asmj.marketplace.admin.repository.AdminAuditLogRepository;
import com.asmj.marketplace.approval.model.Approval;
import com.asmj.marketplace.approval.service.ApprovalService;
import com.asmj.marketplace.post.model.Post;
import com.asmj.marketplace.post.service.PostService;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.user.service.UserService;
import com.asmj.marketplace.vendor.model.Vendor;
import com.asmj.marketplace.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository users;
    private final UserService userService;
    private final VendorService vendors;
    private final PostService posts;
    private final ApprovalService approvals;
    private final AdminAuditLogRepository auditLogs;

    public Map<String, Object> dashboard() {
        long pendingPosts = posts.pending().size();
        long pendingVendors = vendors.pending().size();
        return new LinkedHashMap<>(Map.of(
                "users", users.count(),
                "vendors", vendors.all().size(),
                "posts", posts.all().size(),
                "pendingApprovals", pendingPosts + pendingVendors,
                "pendingPosts", pendingPosts,
                "pendingVendors", pendingVendors
        ));
    }

    public Post approvePost(String id, boolean ok, String adminEmail, String comments) {
        Post post = posts.approve(id, ok);
        approvals.record(id, Approval.TargetType.POST,
                ok ? Approval.Action.APPROVE : Approval.Action.REJECT, adminEmail, comments);
        audit(adminEmail,
                ok ? AdminAuditLog.Action.APPROVE_POST : AdminAuditLog.Action.REJECT_POST,
                AdminAuditLog.TargetType.POST, id, comments);
        return post;
    }

    public Vendor approveVendor(String id, boolean ok, String adminEmail, String comments) {
        Vendor vendor = vendors.setApproval(id, ok);
        approvals.record(id, Approval.TargetType.VENDOR,
                ok ? Approval.Action.APPROVE : Approval.Action.REJECT, adminEmail, comments);
        audit(adminEmail,
                ok ? AdminAuditLog.Action.APPROVE_VENDOR : AdminAuditLog.Action.REJECT_VENDOR,
                AdminAuditLog.TargetType.VENDOR, id, comments);
        return vendor;
    }

    public List<User> users() {
        return userService.all();
    }

    public User changeUserStatus(String id, User.Status status, String adminEmail) {
        User user = userService.setStatus(id, status);
        AdminAuditLog.Action action = switch (status) {
            case ACTIVE -> AdminAuditLog.Action.ACTIVATE_USER;
            case INACTIVE -> AdminAuditLog.Action.DEACTIVATE_USER;
            case SUSPENDED -> AdminAuditLog.Action.SUSPEND_USER;
        };
        audit(adminEmail, action, AdminAuditLog.TargetType.USER, id, "Status changed to " + status);
        return user;
    }

    public User addRole(String id, User.Role role, String adminEmail) {
        User user = userService.addRole(id, role);
        audit(adminEmail, AdminAuditLog.Action.ADD_ROLE, AdminAuditLog.TargetType.USER, id, "Role added: " + role);
        return user;
    }

    public User removeRole(String id, User.Role role, String adminEmail) {
        User user = userService.removeRole(id, role);
        audit(adminEmail, AdminAuditLog.Action.REMOVE_ROLE, AdminAuditLog.TargetType.USER, id, "Role removed: " + role);
        return user;
    }

    public List<AdminAuditLog> auditHistory() {
        return auditLogs.findAllByOrderByCreatedAtDesc();
    }

    public List<AdminAuditLog> auditHistory(String targetId) {
        return auditLogs.findByTargetIdOrderByCreatedAtDesc(targetId);
    }

    private void audit(String actorEmail, AdminAuditLog.Action action, AdminAuditLog.TargetType type,
                       String targetId, String details) {
        auditLogs.save(AdminAuditLog.builder()
                .actorEmail(actorEmail)
                .action(action)
                .targetType(type)
                .targetId(targetId)
                .details(details == null ? "" : details)
                .createdAt(Instant.now())
                .build());
    }
}
