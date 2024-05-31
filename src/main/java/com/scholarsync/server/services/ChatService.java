package com.scholarsync.server.services;

import com.scholarsync.server.dtos.*;
import com.scholarsync.server.entities.*;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChatService {

  @Autowired ChatRepository chatRepository;

  @Autowired FileRepository fileRepository;

  @Autowired GroupRepository groupRepository;

  private final SimpMessagingTemplate sender;
  @Autowired private SessionRepository sessionRepository;
  @Autowired private UserRepository userRepository;

  @Autowired
  public ChatService(SimpMessagingTemplate sender) {
    this.sender = sender;
  }

  public ResponseEntity<Object> deleteChat(String chatId, String userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat/not-found");
    if (!isUserInGroup(user.get(), chat.get().getGroup()))
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user/not-in-chat");
    for (Files file : chat.get().getFiles()) {
      fileRepository.deleteById(file.getId());
    }
    for (User chatUser : chat.get().getGroup().getUsers()) {
      sender.convertAndSend("/individual/" + chatUser.getId() + "/chat", "chat/deleted");
    }
    chatRepository.deleteById(chatId);
    return ResponseEntity.ok("chat/deleted");
  }

  public void deleteChatFile(String chatFileId, String chatId, String userId) {
    Optional<Files> chatFile = fileRepository.findById(chatFileId);
    if (chatFile.isEmpty()) {
      ErrorMessage errorMessage = new ErrorMessage();
      errorMessage.setMessage("File not found");
      errorMessage.setError("file/not-found");
      sender.convertAndSend("/individual/" + chatFileId + "/error", errorMessage);
      return;
    }
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      userNotFoundError(userId);
      return;
    }
    User user = userOptional.get();
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) {
      chatNotFoundError(userId);
    }
    Files file = chatFile.get();
    sender.convertAndSend("/chat/" + chatId, "file " + file.getFileName() + " deleted");
    fileRepository.deleteById(chatFileId);
  }

  public ResponseEntity<Object> createChat(Group group, String name, User user) {

    if (!isUserInGroup(user, group))
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user/not-in-group");
    if (checkCompositeKey(name, group.getId()))
      return ResponseEntity.status(HttpStatus.CONFLICT).body("chat/exists");

    Chat chat = new Chat();
    chat.setGroup(group);
    chat.setName(name);
    chatRepository.save(chat);

    notifyGroupMembers(group, chat);
    return ResponseEntity.ok(new ChatNotificationDTO(chat.getId(), LocalDateTime.now(), chat.getName(), chat.getGroup().getTitle()));
  }

  private void notifyGroupMembers(Group group, Chat chat) {
    Set<User> users = group.getUsers();
    for (User groupUser : users) {
      Optional<Session> session = sessionRepository.findSessionByUserId(groupUser.getId());
      if (session.isEmpty()) continue;
      sender.convertAndSend(
          "/individual/" + session.get().getId() + "/chat",
          new ChatNotificationDTO(chat.getId(), LocalDateTime.now(), chat.getName(), chat.getGroup().getTitle()));
    }
  }

  public void joinChat(String userId, String chatId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      userNotFoundError(userId);
      return;
    }
    User user = userOptional.get();
    boolean userIsLoggedIn = sessionRepository.existsByUserId(userId);
    if (!userIsLoggedIn) {
      userNotLoggedInError(userId);
      return;
    }
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) {
      chatNotFoundError(userId);
      return;
    }
    chat.get().setUserCount(chat.get().getUserCount() + 1);
    chatRepository.save(chat.get());
    // send notification user joined chat
    sender.convertAndSend("/chat/" + chatId, "user " + user.getUsername() + " joined chat");
  }

  public void leaveChat(String userId, String chatId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      userNotFoundError(userId);
      return;
    }
    User user = userOptional.get();
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) {
      chatNotFoundError(userId);
      return;
    }
    chat.get().setUserCount(chat.get().getUserCount() - 1);
    chatRepository.save(chat.get());
    sender.convertAndSend("/chat/" + chatId, "user " + user.getUsername() + " left chat");
  }

  public ResponseEntity<Object> getActiveChatsByGroup(String groupId) {
    Optional<Group> groupOptional = groupRepository.findById(groupId);
    if (groupOptional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("group/not-found");
    List<Chat> listChats = chatRepository.findByGroup(groupOptional.get());
    return ResponseEntity.ok(listChats);
  }

  public ResponseEntity<Object> getChatById(String chatId) {
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat/not-found");
    return ResponseEntity.ok(chat.get());
  }

  @Transactional
  public void sendChatMessage(MessageFromUserDTO payload) {
    Optional<User> user = userRepository.findById(payload.sender_id());
    if (user.isEmpty()) {
      userNotFoundError(payload.sender_id());
      return;
    }
    Optional<Chat> chat = chatRepository.findById(payload.chat_id());
    if (chat.isEmpty()) {
      chatNotFoundError(payload.sender_id());
      return;
    }
    if (!isUserInGroup(user.get(), chat.get().getGroup())) {
      userNotInGroupError(payload);
      return;
    }
    MessageFromServerDTO message = MessageFromServerDTO.fromUserToServer(payload, ProfileDTO.userToProfileDTO(user.get()));
    sender.convertAndSend("/chat/" + payload.chat_id(), message);
  }

  private void userNotInGroupError(MessageFromUserDTO payload) {
    ErrorMessage errorMessage = new ErrorMessage();
    errorMessage.setMessage("User not in chat");
    errorMessage.setError("user/not-in-chat");
    sender.convertAndSend("/individual/" + payload.sender_id() + "/error", errorMessage);
    return;
  }

  @Transactional
  public ResponseEntity<Object> uploadFile(MultipartFile file, String chatId, String userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat/not-found");
    if (!isUserInGroup(user.get(), chat.get().getGroup()))
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user/not-in-chat");
    // upload file

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

    String size = String.valueOf(bytes.length);
    FileDTO chatFileContainer = FileDTO.fileToDTO(chatFiles);
    sender.convertAndSend("/chat/" + chatId, chatFileContainer);
    return ResponseEntity.ok("file/uploaded");
  }

  private boolean checkCompositeKey(String name, String groupId) {
    return chatRepository.existsByNameAndGroupId(name, groupId);
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean isUserInGroup(User user, Group group) {
    Set<User> users = group.getUsers();
    return users.contains(user);
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
}
