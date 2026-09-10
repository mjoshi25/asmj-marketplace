package com.asmj.marketplace.chat.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("conversations")
public class Conversation {
    @Id String id;
    @Builder.Default List<String> participants=new ArrayList<>();
    String postId,lastMessage,name,groupImage,createdBy;
    @Builder.Default String conversationType="INDIVIDUAL";
    Instant createdAt,updatedAt;
    @Transient long unreadCount;
}
