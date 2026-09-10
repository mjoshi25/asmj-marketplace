package com.asmj.marketplace.chat.repository;
import com.asmj.marketplace.chat.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.*;
public interface MessageRepository extends MongoRepository<Message,String> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(String conversationId);
    List<Message> findByConversationIdAndMessageContainingIgnoreCaseOrderByCreatedAtAsc(String conversationId,String q);
    long countByConversationIdAndSenderIdNotAndReadFalse(String conversationId,String senderId);
    List<Message> findByConversationIdAndSenderIdNotAndReadFalse(String conversationId,String senderId);
}
