package com.scholarsync.server.services;

import com.scholarsync.server.dtos.FriendRequesInvitationDTO;
import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.FriendRequestRepository;
import com.scholarsync.server.repositories.NotificationRepository;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.types.NotificationType;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FriendRequestService {
  @Autowired private NotificationRepository notificationRepository;

  @Autowired private FriendRequestRepository friendRequestRepository;
  @Autowired private UserRepository userRepository;

  public ResponseEntity<Object> sendFriendRequest(Map<String, String> friendRequestBody) {
    Optional<User> fromEntry = userRepository.findById(friendRequestBody.get("from_id"));
    Optional<User> toEntry = userRepository.findById(friendRequestBody.get("to_id"));

    if (fromEntry.isEmpty() || toEntry.isEmpty()) {
      return ResponseEntity.badRequest().body("user/not-found");
    }

    if (fromEntry.get().getFriends().contains(toEntry.get())) {
      return ResponseEntity.badRequest().body("user/already-a-friend");
    }

    FriendRequest friendRequest = new FriendRequest();
    friendRequest.setFrom(fromEntry.get());
    friendRequest.setTo(toEntry.get());

    if (friendRequestRepository.existsByFromAndTo(fromEntry.get(), toEntry.get())) {
      return ResponseEntity.badRequest().body("friend-request/already-sent");
    }
    friendRequestRepository.save(friendRequest);

    return ResponseEntity.ok("friend-request/sent");
  }

  public ResponseEntity<Object> getAllRequests(String id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body("user/not-found");
    }
    List<FriendRequesInvitationDTO> response =
        user.get().getReceivedFriendRequests().stream()
            .map(
                friendRequest ->
                    new FriendRequesInvitationDTO(
                        friendRequest.getNotificationId(),
                        friendRequest.getFrom().getId(),
                        friendRequest.getTo().getId(),
                        friendRequest.getCreatedAt().toString()))
            .collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<Object> acceptFriendRequest(String idRequest) {
    Optional<FriendRequest> friendRequest = friendRequestRepository.findById(idRequest);
    if (friendRequest.isEmpty()) {
      return ResponseEntity.badRequest().body("friend-request/not-found");
    }
    if (friendRequest.get().getNotificationType() != NotificationType.FRIEND_REQUEST) {
      return ResponseEntity.badRequest().body("notification/not-friend-request");
    }
    FriendRequest request = friendRequest.get();
    User friend1 = request.getTo();
    User friend2 = request.getFrom();

    Set<User> usersFriend1 = friend1.getFriends();
    Set<User> usersFriend2 = friend2.getFriends();

    if (usersFriend1 == null) {
      usersFriend1 = new HashSet<>();
    }
    if (usersFriend2 == null) {
      usersFriend2 = new HashSet<>();
    }

    usersFriend1.add(friend2);
    usersFriend2.add(friend1);
    friend1.setFriends(usersFriend1);
    friend2.setFriends(usersFriend2);
    friendRequestRepository.delete(request);
    userRepository.save(friend1);
    userRepository.save(friend2);
    String message = "friend-request/accepted";
    return new ResponseEntity<>(message, HttpStatus.OK);
  }

  public ResponseEntity<Object> deleteFriendRequest(String idRequest) {
    Optional<FriendRequest> friendRequest = friendRequestRepository.findById(idRequest);
    if (friendRequest.isEmpty()) {
      return ResponseEntity.badRequest().body("friend-request/not-found");
    }
    friendRequestRepository.delete(friendRequest.get());
    return new ResponseEntity<>("friend-request/denied", HttpStatus.OK);
  }

  public ResponseEntity<Object> getRequestStatus(String from, String to) {
    Optional<User> fromUser = userRepository.findById(from);
    Optional<User> toUser = userRepository.findById(to);
    String response;
    HashMap<String, String> res = new HashMap<>();

    if (fromUser.isEmpty() || toUser.isEmpty()) {
      response = "user/not-found";
      res.put("status", response);
    } else if (fromUser.get().getFriends().contains(toUser.get())) {
      response = "friend-request/already-friends";
      res.put("status", response);
    } else if (friendRequestRepository.existsByFromAndTo(fromUser.get(), toUser.get())) {
      response = "friend-request/sent";
      res.put("status", response);
    } else if (friendRequestRepository.existsByFromAndTo(toUser.get(), fromUser.get())) {
      response = "friend-request/received";
      Optional<FriendRequest> friendRequest =
          friendRequestRepository.findByFromAndTo(toUser.get(), fromUser.get());
      res.put("status", response);
      if (friendRequest.isPresent()) {
        String notificationId = friendRequest.get().getNotificationId();
        res.put("notification_id", notificationId);
      }
    } else {
      response = "friend-request/not-sent";
      res.put("status", response);
    }
    return ResponseEntity.ok(res);
  }
}
