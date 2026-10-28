package com.asmj.marketplace.localpromotion.controller;

import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.localpromotion.model.LocalPromotion;
import com.asmj.marketplace.localpromotion.repository.LocalPromotionRepository;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController @RequestMapping("/api/local-promotions") @RequiredArgsConstructor
public class LocalPromotionController {
    private final LocalPromotionRepository promotions;
    private final UserRepository users;
    private final VendorRepository vendors;

    public record CreateRequest(String targetType, String targetId, String packageCode, String paymentProofUrl) {}
    public record ReviewRequest(boolean approved, String note) {}
    public record FeaturedPromotion(String id, String businessName, String targetType, String targetId, String packageName, int durationDays, Instant startsAt, Instant endsAt, long views, long clicks) {}
    public record PromotionMetric(String id, long views, long clicks, String status) {}

    private void expireEnded() {
        Instant now = Instant.now();
        promotions.findByStatusAndEndsAtBefore(LocalPromotion.Status.ACTIVE, now).forEach(p -> { p.setStatus(LocalPromotion.Status.EXPIRED); promotions.save(p); });
    }

    @GetMapping("/featured")
    public ApiResponse<List<FeaturedPromotion>> featured() {
        expireEnded();
        var items = promotions.findByStatusAndEndsAtAfterOrderByStartsAtDesc(LocalPromotion.Status.ACTIVE, Instant.now()).stream()
            .map(p -> new FeaturedPromotion(p.getId(), p.getBusinessName(), p.getTargetType(), p.getTargetId(), p.getPackageName(), p.getDurationDays(), p.getStartsAt(), p.getEndsAt(), p.getViews(), p.getClicks())).toList();
        return ApiResponse.ok("Current verified local promotions", items);
    }

    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<LocalPromotion> create(Authentication auth, @RequestBody CreateRequest request) {
        if (request == null || request.paymentProofUrl() == null || request.paymentProofUrl().isBlank()) throw new IllegalArgumentException("Upload or provide the payment proof URL before submitting.");
        var user = users.findByEmailIgnoreCase(auth.getName()).orElseThrow();
        var vendor = vendors.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("Create your business profile first."));
        if (vendor.getVerificationStatus() != com.asmj.marketplace.vendor.model.Vendor.VerificationStatus.APPROVED) throw new IllegalArgumentException("Your business profile must be approved before requesting a promotion.");
        String code = request.packageCode() == null ? "" : request.packageCode().trim().toUpperCase();
        int days; double amount; String name;
        switch (code) {
            case "SHOP_FEATURE_7D" -> { days=7; amount=99; name="Featured shop · 7 days"; }
            case "OFFER_FEATURE_7D" -> { days=7; amount=149; name="Featured offer · 7 days"; }
            case "HOMEPAGE_7D" -> { days=7; amount=299; name="Homepage feature · 7 days"; }
            case "LOCAL_MONTH_30D" -> { days=30; amount=599; name="Local growth · 30 days"; }
            default -> throw new IllegalArgumentException("Choose a valid promotion package.");
        }
        var p = LocalPromotion.builder().vendorId(vendor.getId()).businessName(vendor.getBusinessName())
            .targetType(request.targetType()==null?"SHOP":request.targetType().trim().toUpperCase()).targetId(request.targetId())
            .packageCode(code).packageName(name).durationDays(days).amountInr(amount).paymentProofUrl(request.paymentProofUrl().trim())
            .status(LocalPromotion.Status.PENDING_VERIFICATION).submittedAt(Instant.now()).views(0).clicks(0).build();
        return ApiResponse.ok("Promotion submitted for payment verification", promotions.save(p));
    }

    @GetMapping("/mine") @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<List<LocalPromotion>> mine(Authentication auth) {
        expireEnded();
        var user = users.findByEmailIgnoreCase(auth.getName()).orElseThrow();
        var vendor = vendors.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("Business profile not found."));
        return ApiResponse.ok("Your local promotions", promotions.findByVendorIdOrderBySubmittedAtDesc(vendor.getId()));
    }

    @GetMapping("/admin") @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<LocalPromotion>> adminAll() { expireEnded(); return ApiResponse.ok("Local promotion requests", promotions.findAll()); }

    @PostMapping("/admin/{id}/review") @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<LocalPromotion> review(@PathVariable String id, @RequestBody ReviewRequest request) {
        var p = promotions.findById(id).orElseThrow(() -> new IllegalArgumentException("Promotion request not found."));
        p.setAdminNote(request.note());
        if (request.approved()) {
            Instant now = Instant.now(); p.setStatus(LocalPromotion.Status.ACTIVE); p.setVerifiedAt(now); p.setStartsAt(now); p.setEndsAt(now.plus(p.getDurationDays(), ChronoUnit.DAYS));
        } else { p.setStatus(LocalPromotion.Status.REJECTED); p.setVerifiedAt(Instant.now()); }
        return ApiResponse.ok("Promotion review saved", promotions.save(p));
    }

    @PostMapping("/{id}/event")
    public ApiResponse<PromotionMetric> event(@PathVariable String id, @RequestParam String type) {
        var p = promotions.findById(id).orElseThrow(() -> new IllegalArgumentException("Promotion not found."));
        if (p.getStatus()!=LocalPromotion.Status.ACTIVE || p.getEndsAt()==null || !p.getEndsAt().isAfter(Instant.now())) throw new IllegalArgumentException("Promotion is not active.");
        if ("view".equalsIgnoreCase(type)) p.setViews(p.getViews()+1);
        else if ("click".equalsIgnoreCase(type)) p.setClicks(p.getClicks()+1);
        else throw new IllegalArgumentException("Event type must be view or click.");
        var saved = promotions.save(p);
        return ApiResponse.ok("Promotion metric recorded", new PromotionMetric(saved.getId(), saved.getViews(), saved.getClicks(), saved.getStatus().name()));
    }
}
