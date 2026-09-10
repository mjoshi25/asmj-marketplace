package com.asmj.marketplace.chat.dto;
import java.util.*;
public final class ChatDtos {
    private ChatDtos() {}
    public record MessageRequest(String message, String messageType, List<AttachmentRequest> attachments,
                                 String replyToMessageId, String forwardedFromMessageId) {}
    public record AttachmentRequest(String publicId,String secureUrl,String fileName,String mediaType,String resourceType){}
    public record GroupRequest(String name,List<String> participantIds){}
    public record UserSummary(String id,String name,String email,String profileImage){}
    public record AddMemberRequest(String userId){}
}
