package com.asmj.marketplace.chat.service;

import com.asmj.marketplace.chat.dto.ChatDtos.*;
import com.asmj.marketplace.chat.model.*;
import com.asmj.marketplace.chat.repository.*;
import com.asmj.marketplace.post.repository.PostRepository;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class ChatService {
    private final ConversationRepository conversations;
    private final MessageRepository messages;
    private final UserRepository users;
    private final PostRepository posts;
    private final VendorRepository vendors;
    private final SimpMessagingTemplate broker;

    private String uid(String email){return users.findByEmailIgnoreCase(email).orElseThrow(()->new IllegalArgumentException("User not found")).getId();}
    private User user(String email){return users.findByEmailIgnoreCase(email).orElseThrow(()->new IllegalArgumentException("User not found"));}

    public List<Conversation> conversations(String e){
        String me=uid(e);
        return conversations.findByParticipantsContainingOrderByUpdatedAtDesc(me).stream().peek(c->c.setUnreadCount(messages.countByConversationIdAndSenderIdNotAndReadFalse(c.getId(),me))).toList();
    }

    public List<UserSummary> searchUsers(String email,String q){
        String me=uid(email); String term=q==null?"":q.trim().toLowerCase();
        return users.findAll().stream().filter(u->!me.equals(u.getId()))
            .filter(u->term.isBlank()||String.valueOf(u.getName()).toLowerCase().contains(term)||String.valueOf(u.getEmail()).toLowerCase().contains(term))
            .limit(30).map(u->new UserSummary(u.getId(),u.getName(),u.getEmail(),u.getProfileImage())).toList();
    }

    public Message send(String e,String cid,MessageRequest req){
        String sender=uid(e); Conversation c=member(cid,sender);
        String text=req==null?null:req.message(); List<Message.Attachment> atts=new ArrayList<>();
        if(req!=null&&req.attachments()!=null) for(AttachmentRequest a:req.attachments()) if(a!=null&&a.secureUrl()!=null&&!a.secureUrl().isBlank())
            atts.add(Message.Attachment.builder().publicId(a.publicId()).secureUrl(a.secureUrl()).fileName(a.fileName()).mediaType(a.mediaType()).resourceType(a.resourceType()).build());
        if((text==null||text.isBlank())&&atts.isEmpty()) throw new IllegalArgumentException("Message or attachment is required");
        String replyPreview=null;
        if(req!=null&&req.replyToMessageId()!=null&&!req.replyToMessageId().isBlank()){
            Message original=messages.findById(req.replyToMessageId()).orElseThrow(()->new IllegalArgumentException("Reply message not found"));
            if(!cid.equals(original.getConversationId())) throw new IllegalArgumentException("Invalid reply message");
            replyPreview=original.getMessage();
        }
        String preview=text==null||text.isBlank()?"📎 Attachment":text.trim();
        if(req!=null&&req.forwardedFromMessageId()!=null&&!req.forwardedFromMessageId().isBlank()) preview="↗ "+preview;
        Message m=messages.save(Message.builder().conversationId(cid).senderId(sender).message(text==null?"":text.trim())
            .messageType(req==null||req.messageType()==null?"TEXT":req.messageType()).attachments(atts).read(false)
            .replyToMessageId(req==null?null:req.replyToMessageId()).forwardedFromMessageId(req==null?null:req.forwardedFromMessageId())
            .createdAt(Instant.now()).build());
        c.setLastMessage(preview); c.setUpdatedAt(Instant.now()); conversations.save(c);
        enrichMessage(m);
        broker.convertAndSend("/topic/chat/"+cid,m);
        return m;
    }

    public Message sendText(String e,String cid,String text){return send(e,cid,new MessageRequest(text,"TEXT",List.of(),null,null));}

    public Conversation start(String e,String postId){
        String uid=uid(e); var p=posts.findById(postId).orElseThrow(()->new IllegalArgumentException("Listing not found"));
        var v=vendors.findById(p.getVendorId()).orElseThrow(()->new IllegalArgumentException("Vendor not found")); String vendorUserId=v.getUserId();
        List<Conversation> existing=conversations.findByParticipantsContainingAndPostIdOrderByUpdatedAtDesc(uid,postId);
        for(Conversation c:existing) if(c.getParticipants()!=null&&c.getParticipants().contains(vendorUserId)&&c.getParticipants().size()==2)return c;
        return conversations.save(Conversation.builder().participants(new ArrayList<>(List.of(uid,vendorUserId))).postId(postId).conversationType("INDIVIDUAL").createdAt(Instant.now()).updatedAt(Instant.now()).build());
    }

    public Conversation direct(String email,String otherId){
        String me=uid(email); if(me.equals(otherId))throw new IllegalArgumentException("You cannot chat with yourself"); users.findById(otherId).orElseThrow(()->new IllegalArgumentException("User not found"));
        for(Conversation c:conversations.findByParticipantsContainingOrderByUpdatedAtDesc(me)) if("INDIVIDUAL".equals(c.getConversationType())&&c.getParticipants()!=null&&c.getParticipants().size()==2&&c.getParticipants().contains(otherId))return c;
        return conversations.save(Conversation.builder().participants(new ArrayList<>(List.of(me,otherId))).conversationType("INDIVIDUAL").createdAt(Instant.now()).updatedAt(Instant.now()).build());
    }

    public Conversation createGroup(String email,GroupRequest req){
        String me=uid(email); if(req==null||req.name()==null||req.name().isBlank())throw new IllegalArgumentException("Group name is required");
        LinkedHashSet<String> ids=new LinkedHashSet<>(); ids.add(me); if(req.participantIds()!=null)ids.addAll(req.participantIds());
        if(ids.size()<2)throw new IllegalArgumentException("Add at least one member");
        if(users.findAllById(ids).size()!=ids.size())throw new IllegalArgumentException("One or more members are invalid");
        return conversations.save(Conversation.builder().name(req.name().trim()).participants(new ArrayList<>(ids)).conversationType("GROUP").createdBy(me).createdAt(Instant.now()).updatedAt(Instant.now()).build());
    }

    public Conversation addMember(String email,String cid,String memberId){
        String me=uid(email); Conversation c=member(cid,me); if(!"GROUP".equals(c.getConversationType()))throw new IllegalArgumentException("Members can only be managed in groups");
        if(!me.equals(c.getCreatedBy()))throw new IllegalArgumentException("Only the group owner can add members");
        users.findById(memberId).orElseThrow(()->new IllegalArgumentException("User not found"));
        if(!c.getParticipants().contains(memberId)){c.getParticipants().add(memberId);c.setUpdatedAt(Instant.now());conversations.save(c);}
        return c;
    }

    public Conversation removeMember(String email,String cid,String memberId){
        String me=uid(email); Conversation c=member(cid,me); if(!"GROUP".equals(c.getConversationType()))throw new IllegalArgumentException("Members can only be managed in groups");
        if(!me.equals(c.getCreatedBy()))throw new IllegalArgumentException("Only the group owner can remove members");
        if(me.equals(memberId))throw new IllegalArgumentException("Owner cannot remove self; use Exit group");
        c.getParticipants().remove(memberId); c.setUpdatedAt(Instant.now()); return conversations.save(c);
    }

    public void leaveGroup(String email,String cid){
        String me=uid(email); Conversation c=member(cid,me); if(!"GROUP".equals(c.getConversationType()))throw new IllegalArgumentException("This is not a group");
        c.getParticipants().remove(me);
        if(me.equals(c.getCreatedBy())){
            if(c.getParticipants().isEmpty()){conversations.delete(c);return;}
            c.setCreatedBy(c.getParticipants().get(0));
        }
        c.setUpdatedAt(Instant.now()); conversations.save(c);
    }

    public List<UserSummary> members(String email,String cid){Conversation c=member(cid,uid(email));return users.findAllById(c.getParticipants()).stream().map(u->new UserSummary(u.getId(),u.getName(),u.getEmail(),u.getProfileImage())).toList();}

    public List<Message> messages(String e,String cid){String me=uid(e);member(cid,me);List<Message> list=messages.findByConversationIdOrderByCreatedAtAsc(cid);markReadInternal(cid,me);list.forEach(this::enrichMessage);return list;}
    public List<Message> searchMessages(String e,String cid,String q){String me=uid(e);member(cid,me);List<Message> list=messages.findByConversationIdAndMessageContainingIgnoreCaseOrderByCreatedAtAsc(cid,q==null?"":q.trim());list.forEach(this::enrichMessage);return list;}
    public void markRead(String e,String cid){markReadInternal(cid,uid(e));}
    private void markReadInternal(String cid,String me){messages.findByConversationIdAndSenderIdNotAndReadFalse(cid,me).forEach(m->{m.setRead(true);messages.save(m);});}
    public void deleteMessage(String e,String cid,String mid){String me=uid(e);member(cid,me);Message m=messages.findById(mid).orElseThrow(()->new IllegalArgumentException("Message not found"));if(!cid.equals(m.getConversationId())||!me.equals(m.getSenderId()))throw new IllegalArgumentException("You can only delete your own messages");messages.delete(m);}

    private void enrichMessage(Message m){users.findById(m.getSenderId()).ifPresent(u->{m.setSenderName(u.getName());m.setSenderImage(u.getProfileImage());});if(m.getReplyToMessageId()!=null)messages.findById(m.getReplyToMessageId()).ifPresent(x->m.setReplyPreview(x.getMessage()));}
    private Conversation member(String cid,String uid){Conversation c=conversations.findById(cid).orElseThrow(()->new IllegalArgumentException("Conversation not found"));if(c.getParticipants()==null||!c.getParticipants().contains(uid))throw new IllegalArgumentException("Not a participant");return c;}
}
