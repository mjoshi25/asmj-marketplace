package com.asmj.marketplace.revenue.controller;
import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.revenue.model.CommissionRule;
import com.asmj.marketplace.revenue.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/admin/revenue") @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class RevenueAdminController {
 private final RevenueService service;
 @GetMapping("/rules") public ApiResponse<?> rules(){return ApiResponse.ok("Commission rules",service.rules());}
 @PutMapping("/rules") public ApiResponse<?> save(@RequestBody CommissionRule r, Authentication a){return ApiResponse.ok("Commission rule saved",service.saveRule(a.getName(),r));}
 @PostMapping("/preview") public ApiResponse<?> preview(@RequestBody Map<String,Object> b){String c=String.valueOf(b.getOrDefault("category","OTHER"));double amount=Double.parseDouble(String.valueOf(b.getOrDefault("amount",0)));return ApiResponse.ok("Fee preview",service.preview(c,amount));}
 @GetMapping("/ledger") public ApiResponse<?> ledger(){return ApiResponse.ok("Fee ledger",service.ledger());}
 @GetMapping("/summary") public ApiResponse<?> summary(){return ApiResponse.ok("Revenue summary",service.summary());}
}
