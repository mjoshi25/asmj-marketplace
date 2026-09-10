package com.asmj.marketplace.enquiry.service;

import com.asmj.marketplace.chat.model.Conversation;
import com.asmj.marketplace.chat.service.ChatService;
import com.asmj.marketplace.enquiry.model.Enquiry;
import com.asmj.marketplace.enquiry.repository.EnquiryRepository;
import com.asmj.marketplace.post.repository.PostRepository;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnquiryService {
    private final EnquiryRepository repo;
    private final PostRepository posts;
    private final UserRepository users;
    private final VendorRepository vendors;
    private final ChatService chatService;

    public Enquiry create(String email, String postId, String type, String message) {
        if (message == null || message.isBlank()) throw new IllegalArgumentException("Enquiry message is required");
        var u = users.findByEmailIgnoreCase(email).orElseThrow();
        var p = posts.findById(postId).orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        var now = Instant.now();
        return repo.save(Enquiry.builder()
                .postId(postId).userId(u.getId()).vendorId(p.getVendorId())
                .type(type == null ? "GENERAL" : type)
                .message(message.trim()).status("OPEN")
                .createdAt(now).updatedAt(now).build());
    }

    public List<Enquiry> mine(String email) {
        var uid = users.findByEmailIgnoreCase(email).orElseThrow().getId();
        return enrichDistinct(repo.findByUserIdOrderByCreatedAtDesc(uid));
    }

    public List<Enquiry> vendor(String email) {
        var uid = users.findByEmailIgnoreCase(email).orElseThrow().getId();
        var vendor = vendors.findByUserId(uid).orElseThrow(() -> new IllegalArgumentException("Vendor profile not found"));
        return enrichDistinct(repo.findByVendorIdOrderByCreatedAtDesc(vendor.getId()));
    }

    public Conversation reply(String email, String enquiryId, String message) {
        if (message == null || message.isBlank()) throw new IllegalArgumentException("Reply message is required");
        var uid = users.findByEmailIgnoreCase(email).orElseThrow().getId();
        var e = repo.findById(enquiryId).orElseThrow(() -> new IllegalArgumentException("Enquiry not found"));
        if (!Objects.equals(uid, e.getUserId()) && !isVendorForEnquiry(uid, e)) {
            throw new IllegalArgumentException("You are not allowed to reply to this enquiry");
        }
        var conversation = chatService.start(email, e.getPostId());
        chatService.sendText(email, conversation.getId(), message.trim());
        e.setStatus("RESPONDED");
        e.setUpdatedAt(Instant.now());
        repo.save(e);
        return conversation;
    }

    private boolean isVendorForEnquiry(String userId, Enquiry e) {
        return vendors.findByUserId(userId).map(v -> Objects.equals(v.getId(), e.getVendorId())).orElse(false);
    }

    private List<Enquiry> enrichDistinct(List<Enquiry> list) {
        Map<String, Enquiry> unique = list.stream().filter(Objects::nonNull).collect(Collectors.toMap(
                Enquiry::getId, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        for (Enquiry e : unique.values()) {
            posts.findById(e.getPostId()).ifPresent(p -> {
                e.setListingTitle(p.getTitle());
                e.setListingType(p.getType());
                e.setListingImage(p.getImages() != null && !p.getImages().isEmpty() ? p.getImages().get(0) : null);
            });
            users.findById(e.getUserId()).ifPresent(u -> {
                e.setCustomerName(u.getName());
                e.setCustomerEmail(u.getEmail());
                e.setCustomerMobile(u.getMobile());
            });
        }
        return new ArrayList<>(unique.values());
    }
}
