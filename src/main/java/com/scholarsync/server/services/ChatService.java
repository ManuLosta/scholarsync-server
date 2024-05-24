package com.scholarsync.server.services;

import com.scholarsync.server.dtos.ErrorMessage;
import com.scholarsync.server.dtos.MessageFromServerDTO;
import com.scholarsync.server.dtos.MessageFromUserDTO;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.ChatFileRepository;
import com.scholarsync.server.repositories.ChatRepository;
import com.scholarsync.server.repositories.SessionRepository;
import com.scholarsync.server.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChatService {

  @Autowired
  ChatRepository chatRepository;

  @Autowired
  ChatFileRepository chatFileRepository;

  private final SimpMessagingTemplate sender;
  @Autowired
  private SessionRepository sessionRepository;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  public ChatService(SimpMessagingTemplate sender) {
    this.sender = sender;
  }


  public void deleteChat(String chatId) {
    chatRepository.deleteById(chatId);
  }

  public void deleteChatFile(String chatFileId) {
    chatFileRepository.deleteById(chatFileId);
  }

  public void deleteChatFiles(String chatId) {
    chatFileRepository.deleteByChatId(chatId);
  }

  public void createChat(Group group, String name, User user) {

    if(!isUserInGroup(user, group)) return;
    if(checkCompositeKey(name, group.getId())) return;

    Chat chat = new Chat();
    chat.setGroup(group);
    chat.setName(name);
    chatRepository.save(chat);

    notifyGroupMembers(group, chat);
  }

  private void notifyGroupMembers(Group group, Chat chat) {
    Set<User> users = group.getUsers();
    for (User groupUser : users) {
      Optional<Session> session = sessionRepository.findSessionByUserId(groupUser.getId());
      if (session.isEmpty()) continue;
      sender.convertAndSend("/individual/" + groupUser.getId() + "/chat", chat.getId());
    }
  }


  public void joinChat(User user, String chatId) {
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return;
    chat.get().setUserCount(chat.get().getUserCount() + 1);
    chatRepository.save(chat.get());
    //send notification user joined chat
    sender.convertAndSend("/chat/" + chatId , "user " + user.getUsername() + " joined chat");
  }

  public void leaveChat(User user, String chatId) {
    Optional<Chat> chat = chatRepository.findById(chatId);
    if (chat.isEmpty()) return;
    chat.get().setUserCount(chat.get().getUserCount() - 1);
    chatRepository.save(chat.get());
    //send notification user left chat
    sender.convertAndSend("/chat/" + chatId , "user " + user.getUsername() + " left chat");
  }

  public List<Chat> getActiveChatsByGroup(Group group) {
    return chatRepository.findByGroup(group);
  }

  public void sendChatMessage(MessageFromUserDTO payload){
    Optional<User> user = userRepository.findById(payload.getSenderId());
    if(user.isEmpty()) {
      ErrorMessage errorMessage = new ErrorMessage();
      errorMessage.setMessage("User not found");
      errorMessage.setError("user/not-found");
      sender.convertAndSend("/individual/" + payload.getSenderId() + "/error", errorMessage);
      return;
    }
    Optional<Chat> chat = chatRepository.findById(payload.getChatId());
    if(chat.isEmpty()) {
      ErrorMessage errorMessage = new ErrorMessage();
      errorMessage.setMessage("Chat not found");
      errorMessage.setError("chat/not-found");
      sender.convertAndSend("/individual/" + payload.getSenderId() + "/error", errorMessage);
      return;
    }
    if(!isUserInGroup(user.get(),chat.get().getGroup())) {
      ErrorMessage errorMessage = new ErrorMessage();
      errorMessage.setMessage("User not in chat");
      errorMessage.setError("user/not-in-chat");
      sender.convertAndSend("/individual/" + payload.getSenderId() + "/error", errorMessage);
      return;
    }
    MessageFromServerDTO message = MessageFromServerDTO.fromUserToServer(payload, user.get());
    sender.convertAndSend("/chat/" + payload.getChatId(), message);
  }


  public boolean isUserInGroup(User user, Group group) {
    Set<User> users = group.getUsers();
    return group.getUsers().contains(user);
  }

  @Transactional
  public ResponseEntity<Object> uploadFile(MultipartFile file, String chatId, String userId) throws IOException {
    Optional<User> user = userRepository.findById(userId);
    if(user.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");
    Optional<Chat> chat = chatRepository.findById(chatId);
    if(chat.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chat/not-found");
    if(!isUserInGroup(user.get(),chat.get().getGroup())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user/not-in-chat");
    //upload file
    byte[] bytes = file.getBytes();
    ChatFile chatFile = new ChatFile();
    chatFile.setData(bytes);
    chatFile.setChat(chat.get());
    chatFile.setFileName(file.getOriginalFilename());
    chatFile.setFileType(file.getContentType());
    chatFileRepository.save(chatFile);
    //send notification file uploaded

    sender.convertAndSend("/chat/" + chatId, chatFile.getId());
    String size = String.valueOf(bytes.length);
    ChatFileContainer chatFileContainer = new ChatFileContainer(chatFile.getFileName(), String.valueOf(chatFile.getData().length), chatFile.getFileType(), user.get().getUsername());
    return ResponseEntity.ok("file/uploaded");
  }

  public boolean checkCompositeKey(String name, String groupId) {
    return chatRepository.existsByNameAndGroupId(name, groupId);
  }


  public static String getRecommendedSize(byte[] byteArray) {
    int sizeInBytes = byteArray.length;

    if (sizeInBytes < 1024) {
      return sizeInBytes + " Bytes";
    }

    double sizeInKB = (double) sizeInBytes / 1024;
    if (sizeInKB < 1024) {
      return String.format("%.2f KB", sizeInKB);
    }

    double sizeInMB = sizeInKB / 1024;
    if (sizeInMB < 1024) {
      return String.format("%.2f MB", sizeInMB);
    }

    double sizeInGB = sizeInMB / 1024;
    return String.format("%.2f GB", sizeInGB);
  }



  record ChatFileContainer(String name, String size, String type, String username){}

}
