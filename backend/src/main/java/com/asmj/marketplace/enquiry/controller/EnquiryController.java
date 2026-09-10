package com.asmj.marketplace.enquiry.controller;

import com.asmj.marketplace.chat.model.Conversation;
import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.enquiry.model.Enquiry;
import com.asmj.marketplace.enquiry.service.EnquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/enquiries")
@RequiredArgsConstructor
public class EnquiryController {
    private final EnquiryService s;

    @PostMapping
    public ApiResponse<Enquiry> create(Authentication a, @RequestParam String postId,
                                       @RequestParam(required = false) String type,
                                       @RequestParam String message) {
        return ApiResponse.ok("Enquiry created", s.create(a.getName(), postId, type, message));
    }

    @GetMapping("/my")
    public ApiResponse<List<Enquiry>> mine(Authentication a) {
        return ApiResponse.ok("Enquiries", s.mine(a.getName()));
    }

    @PostMapping("/{id}/reply")
    public ApiResponse<Conversation> reply(Authentication a, @PathVariable String id,
                                            @RequestParam String message) {
        return ApiResponse.ok("Reply sent", s.reply(a.getName(), id, message));
    }
}
