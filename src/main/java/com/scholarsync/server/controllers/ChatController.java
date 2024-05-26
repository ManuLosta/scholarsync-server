package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.MessageFromUserDTO;
import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.services.ChatService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

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
  public void uploadFile(
      @RequestParam MultipartFile file, @RequestParam String userId, @RequestParam String chatId) {
    chatService.uploadFile(file, chatId, userId);
  }

  @PostMapping("/api/v1/chat/create-chat")
  public ResponseEntity<Object> createChat(@RequestBody Map<String, String> body) {
    String userId = body.get("userId");
    String groupId = body.get("groupId");
    String name = body.get("name");
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
    return chatService.createChat(group, name, user);
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
}
