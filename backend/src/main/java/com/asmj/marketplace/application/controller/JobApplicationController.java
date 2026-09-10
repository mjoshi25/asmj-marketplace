package com.asmj.marketplace.application.controller;

import com.asmj.marketplace.application.model.JobApplication;
import com.asmj.marketplace.application.service.JobApplicationService;
import com.asmj.marketplace.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.Instant;
import java.util.Map;

@RestController @RequestMapping("/api/applications") @RequiredArgsConstructor
public class JobApplicationController {
    private final JobApplicationService service;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ApiResponse<JobApplication> apply(Authentication a, @RequestBody ApplyRequest r) {
        return ApiResponse.ok("Application submitted", service.apply(a.getName(), r.postId(), r.resumeUrl(), r.resumePublicId(), r.coverLetter()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ApiResponse<List<JobApplication>> mine(Authentication a) { return ApiResponse.ok("Applications", service.mine(a.getName())); }

    @GetMapping("/post/{postId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ApiResponse<Boolean> status(Authentication a, @PathVariable String postId) { return ApiResponse.ok("Application status", service.hasApplied(a.getName(), postId)); }

    @GetMapping("/vendor")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<List<JobApplication>> vendor(Authentication a) { return ApiResponse.ok("Vendor applications", service.vendor(a.getName())); }

    @PutMapping("/vendor/{id}/status")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<JobApplication> updateStatus(Authentication a, @PathVariable String id, @RequestBody Map<String,String> body) {
        return ApiResponse.ok("Application status updated", service.updateVendorStatus(a.getName(), id, body.getOrDefault("status", "SUBMITTED"), body.get("note"), parseInstant(body.get("interviewDate")), body.get("interviewNotes")));
    }

    private Instant parseInstant(String value) { if(value==null || value.isBlank()) return null; try{return Instant.parse(value);}catch(Exception e){throw new IllegalArgumentException("Invalid interviewDate. Use ISO-8601 format.");} }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR') or hasRole('ADMIN')")
    public ApiResponse<JobApplication> one(Authentication a, @PathVariable String id) { return ApiResponse.ok("Application", service.one(a.getName(), id)); }

    public record ApplyRequest(String postId, String resumeUrl, String resumePublicId, String coverLetter) {}
}
