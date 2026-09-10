package com.asmj.marketplace.booking.controller;

import com.asmj.marketplace.booking.model.Booking;
import com.asmj.marketplace.booking.service.BookingService;
import com.asmj.marketplace.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ApiResponse<Booking> create(Authentication a, @RequestBody BookingService.CreateRequest r) {
        return ApiResponse.ok("Booking created", service.create(a.getName(), r));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ApiResponse<List<Booking>> mine(Authentication a) {
        return ApiResponse.ok("Bookings", service.mine(a.getName()));
    }

    @PutMapping("/my/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR')")
    public ApiResponse<Booking> cancel(Authentication a, @PathVariable String id) {
        return ApiResponse.ok("Booking cancelled", service.cancelMine(a.getName(), id));
    }

    @GetMapping("/vendor")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<List<Booking>> vendor(Authentication a) {
        return ApiResponse.ok("Vendor bookings", service.vendor(a.getName()));
    }

    @PutMapping("/vendor/{id}/status")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<Booking> updateVendorStatus(Authentication a, @PathVariable String id,
                                                     @RequestBody Map<String,String> body) {
        return ApiResponse.ok("Booking status updated", service.updateVendorStatus(a.getName(), id, body.getOrDefault("status", "PENDING"), body.get("note")));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('VENDOR') or hasRole('ADMIN')")
    public ApiResponse<Booking> one(Authentication a, @PathVariable String id) {
        return ApiResponse.ok("Booking", service.one(a.getName(), id));
    }

}
