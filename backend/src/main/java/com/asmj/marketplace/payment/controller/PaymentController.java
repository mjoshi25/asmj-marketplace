package com.asmj.marketplace.payment.controller;
import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.payment.dto.PaymentDtos.*;
import com.asmj.marketplace.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;
@RestController @RequestMapping("/api/payments") @RequiredArgsConstructor
public class PaymentController { private final PaymentService s; private String id(Authentication a){return a.getName();}
 @PostMapping("/upload") @PreAuthorize("isAuthenticated()") public ApiResponse<?> upload(Authentication a,@RequestBody PaymentRequest r){return ApiResponse.ok("Payment proof submitted for verification",s.submit(id(a), a.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(x->x.equals("ROLE_VENDOR")),r));}
 @GetMapping("/my") @PreAuthorize("isAuthenticated()") public ApiResponse<?> my(Authentication a){return ApiResponse.ok("",s.mine(id(a)));}
 @GetMapping("/booking/{bookingId}") @PreAuthorize("isAuthenticated()") public ApiResponse<?> booking(@PathVariable String bookingId){return ApiResponse.ok("",s.byBooking(bookingId));}
 @GetMapping("/vendor") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<?> vendor(Authentication a){return ApiResponse.ok("",s.vendor(id(a)));}
 @GetMapping("/invoices/my") @PreAuthorize("isAuthenticated()") public ApiResponse<?> myInvoices(Authentication a){return ApiResponse.ok("",s.invoicesForUser(id(a)));}
 @GetMapping("/invoices/vendor") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<?> vendorInvoices(Authentication a){return ApiResponse.ok("",s.invoicesForVendor(id(a)));}
}
