package com.asmj.marketplace.insurance.controller; import com.asmj.marketplace.common.response.ApiResponse;import com.asmj.marketplace.insurance.model.*;import com.asmj.marketplace.insurance.service.InsuranceMarketplaceService;import lombok.RequiredArgsConstructor;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;import static com.asmj.marketplace.insurance.controller.InsuranceDtos.*;
@RestController @RequestMapping("/api/insurance") @RequiredArgsConstructor public class InsuranceController { final InsuranceMarketplaceService s;
 @GetMapping public ApiResponse<List<InsurancePlan>> all(){return ApiResponse.ok("Insurance plans",s.publicPlans());}
 @GetMapping("/{id}") public ApiResponse<InsurancePlan> one(@PathVariable String id){return ApiResponse.ok("Insurance plan",s.get(id));}
 @PostMapping("/vendor") public ApiResponse<InsurancePlan> create(Authentication a,@RequestBody PlanRequest r){return ApiResponse.ok("Insurance plan submitted",s.save(a.getName(),r,null));}
 @PutMapping("/vendor/{id}") public ApiResponse<InsurancePlan> update(Authentication a,@PathVariable String id,@RequestBody PlanRequest r){return ApiResponse.ok("Insurance plan updated",s.save(a.getName(),r,id));}
 @GetMapping("/vendor") public ApiResponse<List<InsurancePlan>> vendor(Authentication a){return ApiResponse.ok("Vendor insurance plans",s.vendorPlans(a.getName()));}
 @PostMapping("/{id}/quote") public ApiResponse<InsuranceQuoteRequest> quote(Authentication a,@PathVariable String id,@RequestBody QuoteRequest r){return ApiResponse.ok("Quote request submitted",s.requestQuote(a.getName(),id,r));}
 @GetMapping("/quotes/my") public ApiResponse<List<InsuranceQuoteRequest>> mine(Authentication a){return ApiResponse.ok("My insurance quotes",s.myQuotes(a.getName()));}
 @GetMapping("/quotes/vendor") public ApiResponse<List<InsuranceQuoteRequest>> vq(Authentication a){return ApiResponse.ok("Vendor insurance quotes",s.vendorQuotes(a.getName()));}
 @PutMapping("/quotes/vendor/{id}") public ApiResponse<InsuranceQuoteRequest> updateQuote(Authentication a,@PathVariable String id,@RequestBody StatusRequest r){return ApiResponse.ok("Quote updated",s.updateQuote(a.getName(),id,r));}
}
