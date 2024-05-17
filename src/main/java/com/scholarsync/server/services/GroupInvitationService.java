package com.scholarsync.server.services;

import com.scholarsync.server.dtos.GroupNotificationDTO;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.*;
import com.scholarsync.server.types.NotificationType;
import com.scholarsync.server.webSocket.CustomNotificationDTO;
import com.scholarsync.server.webSocket.WebSocketNotificationService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GroupInvitationService {
  @Autowired private GroupService groupService;
  @Autowired private GroupInvitationRepository groupInvitationRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private NotificationRepository notificationRepository;
  @Autowired private WebSocketNotificationService webSocketNotificationService;

  public ResponseEntity<Object> sendGroupInvitation(Map<String, Object> groupInvitationBody) {
    String groupId = (String) groupInvitationBody.get("group_id");
    String toId = (String) groupInvitationBody.get("user_id");
    Optional<Group> groupInvitedBy = groupRepository.findById(groupId);
    Optional<User> user = userRepository.findById(toId);
    if (groupInvitedBy.isEmpty()) {
      return ResponseEntity.badRequest().body("group/not-found");
    }
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body("user/not-found");
    }
    groupInvitationRepository
        .findByGroupAndUser(groupInvitedBy.get(), user.get())
        .ifPresent(
            groupInvitation -> {
              ResponseEntity.badRequest().body("group-invitation/already-sent");
            });
    User to = user.get();
    Group group = groupInvitedBy.get();
    GroupInvitation groupInvitation = new GroupInvitation();
    groupInvitation.setInvitedBy(group.getCreatedBy());
    groupInvitation.setGroup(group);
    groupInvitation.setUser(to); // set group and user
    Set<GroupInvitation> groupInvitations = to.getReceivedGroupInvitations();
    if (groupInvitations != null) {
      groupInvitations.add(groupInvitation);
    } else {
      to.setReceivedGroupInvitations(Set.of(groupInvitation));
    } // add invitation to user
    groupInvitationRepository.save(groupInvitation); // save invitation

    // stomp messaging push notification
    CustomNotificationDTO customNotification = new CustomNotificationDTO();
    customNotification.setGroupId(groupId);
    customNotification.setFrom(group.getCreatedBy().getId());
    customNotification.setTo(to.getId());
    customNotification.setNotificationType(NotificationType.GROUP_INVITE);
    webSocketNotificationService.sendNotification(to.getId(), customNotification);
    // --

    return ResponseEntity.ok("group-invitation/sent");
  }

  public ResponseEntity<Object> acceptGroupInvitation(String groupInvitationId) {
    Optional<GroupInvitation> groupInvitation =
        groupInvitationRepository.findById(groupInvitationId);
    if (groupInvitation.isEmpty()) {
      return ResponseEntity.badRequest().body("group-invitation/not-found");
    }
    groupInvitation.get().setAccepted(true);
    Group invitedTo = groupInvitation.get().getGroup();
    User notified = groupInvitation.get().getUser();
    groupService.addUserToGroup(invitedTo, notified); // add user to group
    notified
        .getReceivedGroupInvitations()
        .remove(groupInvitation.get()); // remove invitation from user
    notificationRepository.delete(groupInvitation.get()); // delete invitation
    return ResponseEntity.ok("group-invitation/accepted");
  }

  public ResponseEntity<Object> declineGroupInvitation(String groupInvitationId) {
    Optional<GroupInvitation> groupInvitation =
        groupInvitationRepository.findById(groupInvitationId);
    if (groupInvitation.isEmpty()) {
      return ResponseEntity.badRequest().body("group-invitation/not-found");
    }
    User notified = groupInvitation.get().getUser();
    notified
        .getReceivedGroupInvitations()
        .remove(groupInvitation.get()); // remove invitation from user
    notificationRepository.delete(groupInvitation.get());
    return ResponseEntity.ok("group-invitation/declined");
  }

  public ResponseEntity<Object> getAllGroupInvitations(String userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body("user/not-found");
    }
    Set<GroupInvitation> invitations = user.get().getReceivedGroupInvitations();
    List<GroupNotificationDTO> response = new ArrayList<>();
    invitations.stream()
        .map(GroupNotificationDTO::groupInvitationToDTO)
        .forEach(response::add); // transform into DTO and add to response ListS
    return ResponseEntity.ok(response);
  }
}
