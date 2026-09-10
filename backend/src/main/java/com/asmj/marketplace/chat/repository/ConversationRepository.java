package com.asmj.marketplace.chat.repository;
import com.asmj.marketplace.chat.model.Conversation;import org.springframework.data.mongodb.repository.MongoRepository;import java.util.*;
public interface ConversationRepository extends MongoRepository<Conversation,String>{
 List<Conversation> findByParticipantsContainingOrderByUpdatedAtDesc(String userId);
 List<Conversation> findByParticipantsContainingAndPostIdOrderByUpdatedAtDesc(String userId,String postId);
}
