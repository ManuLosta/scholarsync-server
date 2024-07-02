package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.MessageFromUserDTO;
import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
public class ChatController {

  @Autowired ChatService chatService;
  @Autowired private UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;

  public record JoinChatType(String user_id, String chat_id) {}

  @MessageMapping("/chat/join")
  public void joinChat(@Payload JoinChatType joinChatType) {
    chatService.joinChat(joinChatType.user_id, joinChatType.chat_id);
  }

  @MessageMapping("/chat/leave")
  public void leaveChat(@Payload JoinChatType joinChatType) {
    chatService.leaveChat(joinChatType.user_id, joinChatType.chat_id);
  }

  @MessageMapping("/chat/send-message")
  public void sendMessage(@Payload MessageFromUserDTO messageFromUserDTO) {
    chatService.sendChatMessage(messageFromUserDTO);
  }

  @PostMapping("/api/v1/chat/upload-file")
  public ResponseEntity<Object> uploadFile(
      @RequestParam MultipartFile file, @RequestParam String userId, @RequestParam String chatId) {
    return chatService.uploadFile(file, chatId, userId);
  }


  @PostMapping("/api/v1/chat/ask-join")
  public ResponseEntity<Object> canUserJoinChat(@RequestBody JoinChatType chatRequest) {
    String userId = chatRequest.user_id();
    String chatId = chatRequest.chat_id();

    chatService.AskCanEnterToTheChat(userId, chatId);
    return ResponseEntity.ok("send request ");
  }

  @PostMapping("/api/v1/chat/allow-join-chat")
  public ResponseEntity<Object> allowJoinChat(@RequestBody JoinChatType chatRequest) {
    String userId = chatRequest.user_id();
    String chatId = chatRequest.chat_id();

    chatService.allowEnterToTheChat(userId, chatId);
    return ResponseEntity.ok("allow to enter");

  }
  @PostMapping("/api/v1/chat/not-allow-join-chat")
  public ResponseEntity<Object> noAllowJoinChat(@RequestBody JoinChatType chatRequest) {
    String userId = chatRequest.user_id();
    String chatId = chatRequest.chat_id();

    chatService.notAllowEnterToTheChat(userId, chatId);
    return ResponseEntity.ok("not allow join chat ");

  }





  @PostMapping("/api/v1/chat/create-chat")
  public ResponseEntity<Object> createChat(@RequestBody Map<String, Object> body) {
    String userId = (String) body.get("userId");
    String groupId = (String) body.get("groupId");
    String name = (String) body.get("name");
    Boolean isPublic = Boolean.parseBoolean((String) body.get("isPublic"));

    Object invitedUserIdsObj = body.get("invitedUsers");
    Set<String> invitedUserIds = new HashSet<>();

    if (invitedUserIdsObj instanceof List<?>) {
      for (Object id : (List<?>) invitedUserIdsObj) {
        if (id instanceof String) {
          invitedUserIds.add((String) id);
        }
      }
    }

    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      return ResponseEntity.status(404).body("user/not-found");
    }
    User user = userOptional.get();
    Optional<Group> optionalGroup = groupRepository.findById(groupId);
    if (optionalGroup.isEmpty()) {
      return ResponseEntity.status(404).body("group/not-found");
    }
    Group group = optionalGroup.get();
    return chatService.createChat(group, name, user, isPublic, invitedUserIds);
  }

  @PostMapping("/api/v1/chat/delete-chat")
  public ResponseEntity<Object> deleteChat(@RequestBody Map<String, String> body) {
    String userId = body.get("sender_id");
    String chatId = body.get("chat_id");
    return chatService.deleteChat(chatId, userId);
  }

  @PostMapping("/api/v1/chat/delete-file")
  public void deleteChatFile(@RequestBody Map<String, String> body) {
    String userId = body.get("sender_id");
    String chatId = body.get("chat_id");
    String fileId = body.get("file_id");
    chatService.deleteChatFile(fileId, chatId, userId);
  }

  @GetMapping("/api/v1/chat/get-chat")
  public ResponseEntity<Object> getChat(@RequestParam String chatId) {
    return chatService.getChatById(chatId);
  }

  @GetMapping("/api/v1/chat/get-chats")
  public ResponseEntity<Object> getChats(@RequestParam String groupId) {
    return chatService.getActiveChatsByGroup(groupId);
  }

  @GetMapping("/api/v1/chat/get-chat-members")
  public ResponseEntity<Object> getChatMembers(@RequestParam String chatId) {
    return chatService.getChatMembers(chatId);
  }
}
