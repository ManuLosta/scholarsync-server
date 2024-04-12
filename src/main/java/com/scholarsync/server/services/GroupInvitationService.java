package com.scholarsync.server.services;

import com.scholarsync.server.dtos.GroupDTO;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class GroupInvitationService {
  @Autowired private GroupService groupService;
  @Autowired private GroupInvitationRepository groupInvitationRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private UserRepository userRepository;

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
        .findByGroupAndUserId(groupInvitedBy.get(), user.get())
        .ifPresent(
            groupInvitation -> {
              ResponseEntity.badRequest().body("group-invitation/already-sent");
            });
    User to = user.get();
    GroupInvitation groupInvitation = new GroupInvitation();
    groupInvitation.setGroup(groupInvitedBy.get());
    groupInvitation.setUserId(to); // set group and user
    Set<GroupInvitation> groupInvitations = to.getGroupInvitations();
    if (groupInvitations != null) {
      groupInvitations.add(groupInvitation);
    } else {
      to.setGroupInvitations(Set.of(groupInvitation));
    } // add invitation to user
    groupInvitationRepository.save(groupInvitation); // save invitation
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
    User notified = groupInvitation.get().getUserId();
    groupService.addUserToGroup(invitedTo, notified); // add user to group
    notified.getGroupInvitations().remove(groupInvitation.get()); // remove invitation from user
    groupInvitationRepository.delete(groupInvitation.get()); // delete invitation
    return ResponseEntity.ok("group-invitation/accepted");
  }

  public ResponseEntity<Object> declineGroupInvitation(String groupInvitationId) {
    Optional<GroupInvitation> groupInvitation =
        groupInvitationRepository.findById(groupInvitationId);
    if (groupInvitation.isEmpty()) {
      return ResponseEntity.badRequest().body("group-invitation/not-found");
    }
    User notified = groupInvitation.get().getUserId();
    notified.getGroupInvitations().remove(groupInvitation.get()); // remove invitation from user
    groupInvitationRepository.delete(groupInvitation.get()); // delete invitation
    return ResponseEntity.ok("group-invitation/declined");
  }

  public ResponseEntity<Object> getAllGroupInvitations(String userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body("user/not-found");
    }
    Set<GroupInvitation> invitations = user.get().getGroupInvitations();
    List<GroupDTO> response = new ArrayList<>();
    invitations.stream()
        .map(this::invitationToGroupDTO)
        .forEach(response::add); // transform into DTO and add to response List
    return ResponseEntity.ok(response);
  }

  private GroupDTO invitationToGroupDTO(GroupInvitation invitation) {
    GroupDTO dto = new GroupDTO();
    dto.setName(invitation.getGroup().getTitle());
    dto.setOwnerName(invitation.getGroup().getCreatedBy().getUsername());
    return dto;
  }
}
