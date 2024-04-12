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
    @Autowired
    private GroupInvitationRepository groupInvitationRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Object> sendGroupInvitation(Map<String,Object> groupInvitationBody) {
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
        User to = user.get();
        GroupInvitation groupInvitation = new GroupInvitation();
        groupInvitation.setGroup(groupInvitedBy.get());
        groupInvitation.setUserId(to); //set group and user
        Set<GroupInvitation> groupInvitations = to.getGroupInvitations();
        if (groupInvitations != null) {
            groupInvitations.add(groupInvitation);
        } else {
            to.setGroupInvitations(Set.of(groupInvitation));
        } // add invitation to user
        userRepository.save(to); //update user
        groupInvitationRepository.save(groupInvitation); //save invitation
        return ResponseEntity.ok("group-invitation/sent");
    }

    public ResponseEntity<Object> acceptGroupInvitation(String groupInvitationId) {
        Optional<GroupInvitation> groupInvitation = groupInvitationRepository.findById(groupInvitationId);
        if (groupInvitation.isEmpty()) {
            return ResponseEntity.badRequest().body("group-invitation/not-found");
        }
        groupInvitation.get().setAccepted(true);
        Group invitedTo = groupInvitation.get().getGroup();
        User notified = groupInvitation.get().getUserId();
        Set<Group> userGroups = notified.getGroups();
        if (userGroups != null) {
            userGroups.add(invitedTo);
        } else {
            notified.setGroups(Set.of(invitedTo));
        } // add group to user
        Set<User> participants = invitedTo.getUsers();
        if(participants != null) {
            participants.add(notified);
        } else {
            invitedTo.setUsers(Set.of(notified));
        }
        invitedTo.setUsers(participants); // add user to group
        groupRepository.save(invitedTo); //update group
        notified.getGroupInvitations().remove(groupInvitation.get()); //remove invitation from user
        userRepository.save(notified); //update user
        groupInvitationRepository.delete(groupInvitation.get()); //delete invitation
        return ResponseEntity.ok("group-invitation/accepted");
    }

    public ResponseEntity<Object> declineGroupInvitation(String groupInvitationId) {
        Optional<GroupInvitation> groupInvitation = groupInvitationRepository.findById(groupInvitationId);
        if (groupInvitation.isEmpty()) {
            return ResponseEntity.badRequest().body("group-invitation/not-found");
        }
        User notified = groupInvitation.get().getUserId();
        notified.getGroupInvitations().remove(groupInvitation.get());//remove invitation from user
        userRepository.save(notified); //update user
        groupInvitationRepository.delete(groupInvitation.get()); //delete invitation
        return ResponseEntity.ok("group-invitation/declined");
    }

    public ResponseEntity<Object> getAllGroupInvitations(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("user/not-found");
        }
        Set<GroupInvitation> invitations =  user.get().getGroupInvitations();
        List<GroupDTO> response = new ArrayList<>();
        invitations.stream().map(this::invitationToGroupDTO).forEach(response::add); //transform into DTO and add to response List
        return ResponseEntity.ok(response);
    }

    private GroupDTO invitationToGroupDTO(GroupInvitation invitation) {
        GroupDTO dto = new GroupDTO();
        dto.setName(invitation.getGroup().getTitle());
        dto.setOwnerName(invitation.getGroup().getCreatedBy().getUsername());
        return dto;
    }
}
