package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.MessageFromAnonymousDTO;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.services.GlobalChatService;
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

import java.util.Map;

@Controller
public class GlobalChatController {

  @Autowired
  GlobalChatService globalChatService;
  @Autowired private UserRepository userRepository;


  public record JoinChatType(String user_id, String chat_id) {}
  public record AccessRequestType(String username, String chat_id) {}


  @MessageMapping("/chat/send-anonymous-message")
  public void sendAnonymousMessage(@Payload MessageFromAnonymousDTO messageFromAnonymousDTO) {
    globalChatService.sendAnonymousChatMessage(messageFromAnonymousDTO);
  }

  @MessageMapping("/chat/request-anonymous-access")
  public void requestAnonymousAccess(@Payload AccessRequestType accessRequestType) {
    globalChatService.accessAnonymousRequest(accessRequestType.username, accessRequestType.chat_id);
  }

  @MessageMapping("/chat/request-access")
  public void requestAccess(@Payload JoinChatType accessRequestType) {
    globalChatService.accessRequest(accessRequestType.user_id, accessRequestType.chat_id);
  }



  @MessageMapping("/chat/accept-anonymous-access")
  public void acceptAnonymousAccess(@Payload AccessRequestType accessRequestType) {
    globalChatService.acceptAnonymousRequest(accessRequestType.chat_id, accessRequestType.username);
  }

  @MessageMapping("/chat/accept-access")
  public void acceptAccess(@Payload JoinChatType accessRequestType) {
    globalChatService.acceptRequest(accessRequestType.chat_id, accessRequestType.user_id);
  }

  @PostMapping("/api/v1/global-chat/create")
  public ResponseEntity<Object> createGlobalChat(@RequestBody Map<String, String> body) {
    String name = body.get("name");
    String userId = body.get("userId");
    return globalChatService.createGlobalChat(name, userId);
  }

  @GetMapping("/api/v1/global-chat/list-anonymous-members")
  public ResponseEntity<Object> listAnonymousUsers(@RequestParam String chatId) {
    return globalChatService.listAnonymousMembers(chatId);
  }

  @PostMapping("/api/v1/global-chat/upload-anonymous-file")
  public ResponseEntity<Object> uploadAnonymousFile(
          @RequestParam MultipartFile file, @RequestParam String chatId, @RequestParam String username) {
    return globalChatService.uploadAnonymousFile(file, chatId, username);
  }

  @PostMapping("/api/v1/global-chat/leave")
  public ResponseEntity<Object> leaveChat(@RequestBody Map<String, String> body) {
    String userId = body.get("userId");
    String chatId = body.get("chatId");
    return globalChatService.leaveAnonymousChat(userId, chatId);
  }

  @GetMapping("/api/v1/global-chat/get-global-chat")
  public ResponseEntity<Object> getGlobalChat(@RequestParam String userId) {
    return globalChatService.getGlobalChats(userId);
  }

  @GetMapping("/api/v1/global-chat/get-chat")
  public ResponseEntity<Object> getChat(@RequestParam String chatId) {
    return globalChatService.getChatById(chatId);
  }
}
