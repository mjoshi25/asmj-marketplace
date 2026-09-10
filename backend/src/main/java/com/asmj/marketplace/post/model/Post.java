package com.asmj.marketplace.post.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("posts")
public class Post {
    @Id String id;
    @Indexed String vendorId, categoryId;
    @Indexed String type, title, slug, description;
    Double price;
    String priceType;
    List<String> images;
    List<MediaItem> attachments;
    Location location;
    Map<String,Object> attributes;
    PostStatus status;
    PostStatus approvalStatus;
    long views;
    boolean featured;
    Instant createdAt, updatedAt;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class MediaItem {
        String publicId;
        String secureUrl;
        String resourceType;
        String fileName;
        String mediaType;
        String purpose;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Location {
        String country,state,city,area,pincode;
        Double latitude,longitude;
    }

    public enum PostStatus { DRAFT,SUBMITTED,PENDING_APPROVAL,APPROVED,REJECTED,PUBLISHED,SUSPENDED,EXPIRED,ARCHIVED }
}
