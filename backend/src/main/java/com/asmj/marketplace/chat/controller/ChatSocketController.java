package com.asmj.marketplace.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatSocketController {
    @MessageMapping("/chat/{conversationId}/typing")
    @SendTo("/topic/chat/{conversationId}/typing")
    public TypingEvent typing(@DestinationVariable String conversationId, TypingEvent event) {
        return event;
    }
    public record TypingEvent(String userId,String userName,boolean typing) {}
}
