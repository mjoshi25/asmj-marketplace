package com.asmj.marketplace.job.controller;
import com.asmj.marketplace.job.model.*; import com.asmj.marketplace.job.service.JobMarketplaceService; import com.asmj.marketplace.common.response.ApiResponse; import lombok.RequiredArgsConstructor; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/jobs") @RequiredArgsConstructor public class JobController { private final JobMarketplaceService s;
 @GetMapping public ApiResponse<List<JobListing>> publicJobs(){return ApiResponse.ok("Jobs",s.publicJobs());}
 @GetMapping("/{id}") public ApiResponse<JobListing> one(@PathVariable String id){return ApiResponse.ok("Job",s.one(id));}
 @PostMapping("/vendor") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<JobListing> create(Authentication a,@RequestBody JobMarketplaceService.CreateJob r){return ApiResponse.ok("Job submitted for approval",s.create(a.getName(),r));}
 @GetMapping("/vendor") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<List<JobListing>> mine(Authentication a){return ApiResponse.ok("Jobs",s.myJobs(a.getName()));}
 @GetMapping("/vendor/employer") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<EmployerProfile> employer(Authentication a){return ApiResponse.ok("Employer",s.myEmployer(a.getName()));}
 @PutMapping("/vendor/employer") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<EmployerProfile> employer(Authentication a,@RequestBody EmployerProfile p){return ApiResponse.ok("Employer profile saved",s.saveEmployer(a.getName(),p));}
 @PostMapping("/vendor/applications/{id}/interview") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<JobInterview> interview(Authentication a,@PathVariable String id,@RequestBody JobMarketplaceService.InterviewRequest r){return ApiResponse.ok("Interview scheduled",s.scheduleInterview(a.getName(),id,r));}
 @GetMapping("/vendor/interviews") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<List<JobInterview>> vendorInterviews(Authentication a){return ApiResponse.ok("Interviews",s.vendorInterviews(a.getName()));}
 @PostMapping("/vendor/applications/{id}/offer") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<JobOffer> offer(Authentication a,@PathVariable String id,@RequestBody JobMarketplaceService.OfferRequest r){return ApiResponse.ok("Offer sent",s.createOffer(a.getName(),id,r));}
 @GetMapping("/vendor/offers") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<List<JobOffer>> vendorOffers(Authentication a){return ApiResponse.ok("Offers",s.vendorOffers(a.getName()));}
 @GetMapping("/interviews/my") @PreAuthorize("hasRole('USER') or hasRole('VENDOR')") public ApiResponse<List<JobInterview>> myInterviews(Authentication a){return ApiResponse.ok("Interviews",s.myInterviews(a.getName()));}
 @GetMapping("/offers/my") @PreAuthorize("hasRole('USER') or hasRole('VENDOR')") public ApiResponse<List<JobOffer>> myOffers(Authentication a){return ApiResponse.ok("Offers",s.myOffers(a.getName()));}
 @PutMapping("/offers/{id}/response") @PreAuthorize("hasRole('USER') or hasRole('VENDOR')") public ApiResponse<JobOffer> respond(Authentication a,@PathVariable String id,@RequestParam String status){return ApiResponse.ok("Offer updated",s.respondOffer(a.getName(),id,status));}
}
