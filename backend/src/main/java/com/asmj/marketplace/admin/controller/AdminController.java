package com.asmj.marketplace.admin.controller;

import com.asmj.marketplace.admin.model.AdminAuditLog;
import com.asmj.marketplace.admin.service.AdminService;
import com.asmj.marketplace.approval.model.Approval;
import com.asmj.marketplace.approval.service.ApprovalService;
import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.post.model.Post;
import com.asmj.marketplace.post.service.PostService;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.vendor.model.Vendor;
import com.asmj.marketplace.vendor.service.VendorService;
import com.asmj.marketplace.printout.model.PrintoutService;
import com.asmj.marketplace.printout.service.PrintoutServiceManager;import com.asmj.marketplace.review.model.Review;import com.asmj.marketplace.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService admin;
    private final ApprovalService approvalService;
    private final PostService posts;
    private final VendorService vendors;private final ReviewRepository reviews;
    private final PrintoutServiceManager printouts;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.ok("Admin dashboard", admin.dashboard());
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> users() {
        return ApiResponse.ok("Users", admin.users().stream().map(this::safe).toList());
    }

    @PutMapping("/users/{id}/status")
    public ApiResponse<User> changeUserStatus(@PathVariable String id,
                                               @RequestBody Map<String, String> body,
                                               Authentication authentication) {
        User.Status status = User.Status.valueOf(body.getOrDefault("status", "ACTIVE").toUpperCase());
        return ApiResponse.ok("User status updated", safe(admin.changeUserStatus(id, status, authentication.getName())));
    }

    @PutMapping("/users/{id}/roles/{role}")
    public ApiResponse<User> addRole(@PathVariable String id, @PathVariable User.Role role, Authentication authentication) {
        return ApiResponse.ok("Role added", safe(admin.addRole(id, role, authentication.getName())));
    }

    @DeleteMapping("/users/{id}/roles/{role}")
    public ApiResponse<User> removeRole(@PathVariable String id, @PathVariable User.Role role, Authentication authentication) {
        return ApiResponse.ok("Role removed", safe(admin.removeRole(id, role, authentication.getName())));
    }

    @GetMapping("/listings")
    public ApiResponse<List<Post>> listings() {
        return ApiResponse.ok("All listings", posts.adminAll());
    }

    @PutMapping("/posts/{id}/suspend")
    public ApiResponse<Post> suspend(@PathVariable String id) {
        return ApiResponse.ok("Listing suspended", posts.changeAdminStatus(id, Post.PostStatus.SUSPENDED));
    }

    @PutMapping("/posts/{id}/reactivate")
    public ApiResponse<Post> reactivate(@PathVariable String id) {
        return ApiResponse.ok("Listing reactivated", posts.changeAdminStatus(id, Post.PostStatus.PUBLISHED));
    }

    @GetMapping("/approvals")
    public ApiResponse<List<Post>> approvals() {
        return ApiResponse.ok("Pending posts", posts.pending());
    }

    @GetMapping("/vendors/pending")
    public ApiResponse<List<Vendor>> pendingVendors() {
        return ApiResponse.ok("Pending vendors", vendors.pending());
    }

    @PutMapping("/posts/{id}/approve")
    public ApiResponse<Post> approve(@PathVariable String id, Authentication authentication) {
        return ApiResponse.ok("Approved", admin.approvePost(id, true, authentication.getName(), ""));
    }

    @PutMapping("/posts/{id}/reject")
    public ApiResponse<Post> reject(@PathVariable String id,
                                    @RequestBody(required = false) Map<String, String> body,
                                    Authentication authentication) {
        String comments = body == null ? "" : body.getOrDefault("comments", "");
        return ApiResponse.ok("Rejected", admin.approvePost(id, false, authentication.getName(), comments));
    }

    @GetMapping("/vendors")
    public ApiResponse<List<Vendor>> vendorList() {
        return ApiResponse.ok("Vendors", vendors.all());
    }

    @PutMapping("/vendors/{id}/approve")
    public ApiResponse<Vendor> approveVendor(@PathVariable String id, Authentication authentication) {
        return ApiResponse.ok("Vendor approved", admin.approveVendor(id, true, authentication.getName(), ""));
    }

    @PutMapping("/vendors/{id}/reject")
    public ApiResponse<Vendor> rejectVendor(@PathVariable String id,
                                             @RequestBody(required = false) Map<String, String> body,
                                             Authentication authentication) {
        String comments = body == null ? "" : body.getOrDefault("comments", "");
        return ApiResponse.ok("Vendor rejected", admin.approveVendor(id, false, authentication.getName(), comments));
    }

    @GetMapping("/approvals/{targetId}/details")
    public ApiResponse<Map<String,Object>> approvalDetails(@PathVariable String targetId) {
        Post post = posts.preview(targetId);
        Vendor vendor = vendors.all().stream().filter(v -> Objects.equals(v.getId(), post.getVendorId())).findFirst().orElse(null);
        return ApiResponse.ok("Approval details", Map.of(
                "post", post,
                "vendor", vendor == null ? Map.of() : vendor,
                "history", approvalService.history(targetId)
        ));
    }


    @GetMapping("/vendors/{id}/details")
    public ApiResponse<Map<String,Object>> vendorDetails(@PathVariable String id) {
        Vendor vendor = vendors.all().stream().filter(v -> Objects.equals(v.getId(), id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        return ApiResponse.ok("Vendor details", Map.of("vendor", vendor, "history", approvalService.history(id)));
    }

    @GetMapping("/approvals/{targetId}/history")
    public ApiResponse<List<Approval>> history(@PathVariable String targetId) {
        return ApiResponse.ok("Approval history", approvalService.history(targetId));
    }


    @GetMapping("/printouts/pending")
    public ApiResponse<List<PrintoutService>> pendingPrintouts() {
        return ApiResponse.ok("Pending printout services", printouts.pendingApprovals());
    }

    @PutMapping("/printouts/{id}/approve")
    public ApiResponse<PrintoutService> approvePrintout(@PathVariable String id) {
        return ApiResponse.ok("Printout service approved", printouts.approve(id));
    }

    @PutMapping("/printouts/{id}/reject")
    public ApiResponse<PrintoutService> rejectPrintout(@PathVariable String id) {
        return ApiResponse.ok("Printout service rejected", printouts.reject(id));
    }

    @GetMapping("/reviews") public ApiResponse<List<Review>> reviews(){return ApiResponse.ok("Reviews",reviews.findAllByOrderByCreatedAtDesc());}

    @DeleteMapping("/reviews/{id}") public ApiResponse<Void> deleteReview(@PathVariable String id, Authentication authentication){reviews.deleteById(id);return ApiResponse.ok("Review deleted",null);}

    @GetMapping("/audit-logs")
    public ApiResponse<List<AdminAuditLog>> auditLogs() {
        return ApiResponse.ok("Admin audit logs", admin.auditHistory());
    }

    @GetMapping("/audit-logs/{targetId}")
    public ApiResponse<List<AdminAuditLog>> auditLogs(@PathVariable String targetId) {
        return ApiResponse.ok("Admin audit logs", admin.auditHistory(targetId));
    }

    private User safe(User user) {
        user.setPassword(null);
        return user;
    }
}
