package com.scholarsync.server.services;


import com.scholarsync.server.dtos.*;
import com.scholarsync.server.entities.Chat;
import com.scholarsync.server.entities.Files;
import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class GlobalChatService {

  @Autowired ChatRepository chatRepository;

  @Autowired
  FileRepository fileRepository;

  @Autowired
  GroupRepository groupRepository;

  private final SimpMessagingTemplate sender;
  @Autowired private SessionRepository sessionRepository;
  @Autowired private UserRepository userRepository;

  @Autowired
  public GlobalChatService(SimpMessagingTemplate sender) {
    this.sender = sender;
  }


  public ResponseEntity<Object> createGlobalChat(String name, String userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
    User user = userOptional.get();
    Chat chat = new Chat();
    chat.setName(name);
    user.setChat(chat);
    chat.setOwnerId(user.getId());
    chatRepository.save(chat);
    userRepository.save(user);
    return ResponseEntity.ok(new ChatNotificationDTO(chat.getId(), LocalDateTime.now(), chat.getName(), null));
  }

  public record ChatAccessUserRequest(String chatId, String userId){};

  @Transactional
  public void accessAnonymousRequest(String chatId, String username) {
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return;
    if(chat.get().getAnonymousUsers() == null) {
      sender.convertAndSend("/individual/" + chat.get().getOwnerId() + "/chat-access-request", new ChatAccessRequest(chatId, username));
      return;
    }
    if(chat.get().getAnonymousUsers().contains(username)) {
      sender.convertAndSend("/individual/" + username + "/error", "username-taken");
      return;
    }
    sender.convertAndSend("/individual/" + chat.get().getOwnerId() + "/chat-access-request", new ChatAccessRequest(chatId, username));
    return;
  }

  @Transactional
  public void accessRequest(String userId, String chatId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      userNotFoundError(userId);
      return;
    }
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) {
      chatNotFoundError(userId);
      return;
    }
    sender.convertAndSend(
        "/individual/" + chat.get().getOwnerId() + "/chat-access-request",
        new ChatAccessUserRequest(chatId, user.get().getId()));
  }

  @Transactional
  public void acceptAnonymousRequest(String chatId, String username) {
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return;
    if(chat.get().getAnonymousUsers() == null) chat.get().setAnonymousUsers(username);
    else chat.get().setAnonymousUsers(chat.get().getAnonymousUsers() + "," + username);
    chatRepository.save(chat.get());
    sender.convertAndSend("/individual/" + username + "/chat-request-accepted", chatId);
    sender.convertAndSend("/chat/" + chatId + "/info", new ChatInfoNotification(chat.get().getUsers().size(), username, true));
    return;
  }


  @Transactional
  public void acceptRequest(String chatId, String userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      userNotFoundError(userId);
      return;
    }
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) {
      chatNotFoundError(userId);
      return;
    }
    chat.get().getUsers().add(user.get());
    chatRepository.save(chat.get());
    user.get().setChat(chat.get());
    userRepository.save(user.get());
    sender.convertAndSend("/individual/" + userId + "/chat-request-accepted", chatId);
    sender.convertAndSend("/chat/" + chatId + "/info", new ChatInfoNotification(chat.get().getUsers().size(), user.get().getUsername(), true));
  }


  @Transactional
  public ResponseEntity<Object> listAnonymousMembers(String chatId) {
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat/not-found");
    String[] anonymousUsers = chat.get().getAnonymousUsers().split(",");
    return ResponseEntity.ok(anonymousUsers);
  }

  @Transactional
  public ResponseEntity<Object> uploadAnonymousFile(MultipartFile file, String chatId, String username) {
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat/not-found");


    byte[] bytes;
    try {
      bytes = file.getBytes();
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("file/not-found");
    }
    Files chatFiles = new Files();
    chatFiles.setFile(bytes);
    chatFiles.setFileName(file.getOriginalFilename());
    chatFiles.setFileType(file.getContentType());
    fileRepository.save(chatFiles);
    // send notification file uploaded

    FileDTO chatFileContainer = FileDTO.fileToDTO(chatFiles);
    FileFromUserDTO response = FileFromUserDTO.fromUserToServer(chatFileContainer, ProfileDTO.anonymousUser(username));
    sender.convertAndSend("/chat/" + chatId + "/files", response);
    return ResponseEntity.ok("file/uploaded");
  }

  @Transactional
  public ResponseEntity<Object> leaveAnonymousChat(String username, String chatId) {
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat/not-found");
    if(chat.get().getAnonymousUsers() == null) return  ResponseEntity.ok("users/empty");
    if (!chat.get().getAnonymousUsers().contains(username)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");
    String[] anonymousUsers = chat.get().getAnonymousUsers().split(",");
    StringBuilder newUsers = new StringBuilder();
    for (String user : anonymousUsers) {
      if (!user.equals(username)) {
        newUsers.append(user).append(",");
      }
    }
    chat.get().setAnonymousUsers(newUsers.toString());
    chatRepository.save(chat.get());
    sender.convertAndSend("/chat/" + chatId + "/info", new ChatInfoNotification(chat.get().getUsers().size(), username, false));
    return ResponseEntity.ok("chat/left");
  }

  @Transactional
  public void sendAnonymousChatMessage(MessageFromAnonymousDTO payload) {
    MessageFromServerDTO message = MessageFromServerDTO.fromAnonymousToServer(payload, ProfileDTO.anonymousUser(payload.username()));
    sender.convertAndSend("/chat/" + payload.chat_id(), message);
  }

  @Transactional
  public ResponseEntity<Object> getChatById(String id) {
    Optional<Chat> chat = chatRepository.findById(id);
    if(chat.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat/not-found");
    return ResponseEntity.ok(ChatDTO.fromEntity(chat.get()));
  }


  private void chatNotFoundError(String userId) {
    ErrorMessage errorMessage = new ErrorMessage();
    errorMessage.setMessage("Chat not found");
    errorMessage.setError("chat/not-found");
    sender.convertAndSend("/individual/" + userId + "/error", errorMessage);
    return;
  }

  private void userNotLoggedInError(String userId) {
    ErrorMessage errorMessage = new ErrorMessage();
    errorMessage.setMessage("User not logged in");
    errorMessage.setError("user/not-logged-in");
    sender.convertAndSend("/individual/" + userId + "/error", errorMessage);
    return;
  }

  private void userNotFoundError(String userId) {
    ErrorMessage errorMessage = new ErrorMessage();
    errorMessage.setMessage("User not found");
    errorMessage.setError("user/not-found");
    sender.convertAndSend("/individual/" + userId + "/error", errorMessage);
    return;
  }

  @Transactional
  public ResponseEntity<Object> getGlobalChats(String userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
    User user = userOptional.get();
    Chat chat = user.getChat();
    if (chat == null) {
      String res = "chat/not-found";
      return ResponseEntity.ok(res);
    }
    return ResponseEntity.ok(ChatDTO.fromEntity(chat));
  }


}
