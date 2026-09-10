package com.asmj.marketplace.chat.controller;
import com.asmj.marketplace.chat.dto.ChatDtos.*;
import com.asmj.marketplace.chat.model.*;
import com.asmj.marketplace.chat.service.ChatService;
import com.asmj.marketplace.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/chat") @RequiredArgsConstructor
public class ChatController {
 private final ChatService s;
 @GetMapping("/conversations") public ApiResponse<List<Conversation>> list(Authentication a){return ApiResponse.ok("Conversations",s.conversations(a.getName()));}
 @PostMapping("/conversations") public ApiResponse<Conversation> start(Authentication a,@RequestParam String postId){return ApiResponse.ok("Conversation",s.start(a.getName(),postId));}
 @PostMapping("/direct") public ApiResponse<Conversation> direct(Authentication a,@RequestParam String userId){return ApiResponse.ok("Direct conversation",s.direct(a.getName(),userId));}
 @PostMapping("/groups") public ApiResponse<Conversation> group(Authentication a,@RequestBody GroupRequest req){return ApiResponse.ok("Group created",s.createGroup(a.getName(),req));}
 @GetMapping("/users") public ApiResponse<List<UserSummary>> users(Authentication a,@RequestParam(defaultValue="") String q){return ApiResponse.ok("Users",s.searchUsers(a.getName(),q));}
 @GetMapping("/{id}/members") public ApiResponse<List<UserSummary>> members(Authentication a,@PathVariable String id){return ApiResponse.ok("Members",s.members(a.getName(),id));}
 @PostMapping("/{id}/members") public ApiResponse<Conversation> add(Authentication a,@PathVariable String id,@RequestBody AddMemberRequest req){return ApiResponse.ok("Member added",s.addMember(a.getName(),id,req.userId()));}
 @DeleteMapping("/{id}/members/{userId}") public ApiResponse<Conversation> remove(Authentication a,@PathVariable String id,@PathVariable String userId){return ApiResponse.ok("Member removed",s.removeMember(a.getName(),id,userId));}
 @PostMapping("/{id}/leave") public ApiResponse<Void> leave(Authentication a,@PathVariable String id){s.leaveGroup(a.getName(),id);return ApiResponse.ok("Left group",null);}
 @GetMapping("/{id}/messages") public ApiResponse<List<Message>> messages(Authentication a,@PathVariable String id){return ApiResponse.ok("Messages",s.messages(a.getName(),id));}
 @GetMapping("/{id}/messages/search") public ApiResponse<List<Message>> search(Authentication a,@PathVariable String id,@RequestParam String q){return ApiResponse.ok("Messages",s.searchMessages(a.getName(),id,q));}
 @PostMapping("/{id}/read") public ApiResponse<Void> read(Authentication a,@PathVariable String id){s.markRead(a.getName(),id);return ApiResponse.ok("Messages marked read",null);}
 @PostMapping("/{id}/messages") public ApiResponse<Message> send(Authentication a,@PathVariable String id,@RequestBody MessageRequest req){return ApiResponse.ok("Message sent",s.send(a.getName(),id,req));}
 @DeleteMapping("/{id}/messages/{messageId}") public ApiResponse<Void> delete(Authentication a,@PathVariable String id,@PathVariable String messageId){s.deleteMessage(a.getName(),id,messageId);return ApiResponse.ok("Message deleted",null);}
}
