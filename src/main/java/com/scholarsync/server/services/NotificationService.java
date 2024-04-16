package com.scholarsync.server.services;

import com.scholarsync.server.dtos.FriendRequesInvitationDTO;
import com.scholarsync.server.dtos.GroupNotificationDTO;
import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.entities.GroupInvitation;
import com.scholarsync.server.entities.Notification;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.FriendRequestRepository;
import com.scholarsync.server.repositories.GroupInvitationRepository;
import com.scholarsync.server.repositories.NotificationRepository;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.types.NotificationType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  @Autowired NotificationRepository notificationRepository;
  @Autowired UserRepository userRepository;
  @Autowired FriendRequestRepository friendRequestRepository;
  @Autowired GroupInvitationRepository groupInvitationRepository;

  public ResponseEntity<Object> getAllNotifications(String userId) {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      return ResponseEntity.status(404).body("User not found");
    }
    Set<Notification> notifications = user.getReceivedNotifications();
    List<Object> response =
        notifications.stream()
            .map(
                notification -> {
                  switch (notification.getNotifactionType()) {
                    case NotificationType.FRIEND_REQUEST:
                      Optional<FriendRequest> friendRequest =
                          friendRequestRepository.findById(notification.getNotificationId());
                      if (friendRequest.isEmpty()) {
                        break;
                      }
                      return new FriendRequesInvitationDTO(
                          friendRequest.get().getNotificationId(),
                          friendRequest.get().getFrom().getId(),
                          friendRequest.get().getTo().getId(),
                          friendRequest.get().getCreatedAt().toString());
                    case NotificationType.GROUP_INVITE:
                      Optional<GroupInvitation> groupInvitation =
                          groupInvitationRepository.findById(notification.getNotificationId());
                      if (groupInvitation.isEmpty()) {
                        break;
                      }
                      GroupInvitation invitation = groupInvitation.get();
                      GroupNotificationDTO dto = new GroupNotificationDTO();
                      dto.setId(invitation.getNotificationId());
                      dto.setName(invitation.getGroup().getTitle());
                      dto.setOwnerName(invitation.getGroup().getCreatedBy().getUsername());
                      return dto;
                    default:
                      return null;
                  }
                  return null;
                })
            .toList();
    return ResponseEntity.ok(response);
  }
}
