package com.asmj.marketplace.chat.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("messages")
public class Message {
    @Id String id;
    String conversationId, senderId, message, messageType;
    boolean read;
    Instant createdAt;
    String replyToMessageId;
    String forwardedFromMessageId;
    @Builder.Default List<Attachment> attachments = new ArrayList<>();
    @Transient String senderName;
    @Transient String senderImage;
    @Transient String replyPreview;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Attachment {
        String publicId, secureUrl, fileName, mediaType, resourceType;
    }
}
