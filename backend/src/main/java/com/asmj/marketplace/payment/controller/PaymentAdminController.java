package com.asmj.marketplace.payment.controller;
import com.asmj.marketplace.common.response.ApiResponse;import com.asmj.marketplace.payment.dto.PaymentDtos.ReviewRequest;import com.asmj.marketplace.payment.service.PaymentService;import lombok.RequiredArgsConstructor;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/admin/payments") @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class PaymentAdminController { private final PaymentService s;
 @GetMapping public ApiResponse<?> all(){return ApiResponse.ok("",s.all());}
 @PutMapping("/{id}/review") public ApiResponse<?> review(Authentication a,@PathVariable String id,@RequestBody ReviewRequest r){return ApiResponse.ok("Payment reviewed",s.review(a.getName(),id,r));}
 @GetMapping("/invoices") public ApiResponse<?> invoices(){return ApiResponse.ok("",s.allInvoices());}
}
