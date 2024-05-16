package com.scholarsync.server.services;

import com.scholarsync.server.dtos.ProfileDTO;
import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.GroupInvitation;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.GroupInvitationRepository;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.UserRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  @Autowired private GroupInvitationRepository groupInvitationRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private UserRepository userRepository;

  private static List<Map<String, Object>> getGroupList(User user) {
    Set<Group> groups = user.getGroups();
    List<Map<String, Object>> response = new ArrayList<>();
    for (Group group : groups) {
      Map<String, Object> groupMap = new HashMap<>();
      createGroup(group, groupMap);
      response.add(groupMap);
    }
    return response;
  }

  private static void createGroup(Group group, Map<String, Object> groupMap) {
    groupMap.put("id", group.getId());
    groupMap.put("title", group.getTitle());
    groupMap.put("description", group.getDescription());
    groupMap.put("isPrivate", group.isPrivate());
    groupMap.put("createdBy", group.getCreatedBy().getId());
  }

  public ResponseEntity<Object> createGroup(Map<String, Object> group) {
    try {
      Group generatedGroup = new Group();
      generatedGroup.setTitle((String) group.get("title"));
      generatedGroup.setDescription((String) group.get("description"));
      generatedGroup.setPrivate((Boolean) group.get("isPrivate"));
      Optional<User> optionalCreator = userRepository.findById((String) group.get("userId"));
      if (optionalCreator.isEmpty()) {
        return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
      } else {
        User creator = optionalCreator.get();
        generatedGroup.setCreatedBy(creator);
        Set<User> userSet = generatedGroup.getUsers();
        userSet.add(creator);
        generatedGroup.setUsers(userSet);
        generatedGroup.setCreatedBy(creator);
        groupRepository.save(generatedGroup);

        creator.getGroups().add(generatedGroup);
        userRepository.save(creator);

        System.out.println("Group " + generatedGroup.getTitle() + " created successfully!");
        return new ResponseEntity<>("Group Generated", HttpStatus.OK);
      }

    } catch (DataIntegrityViolationException e) {
      String errorMessage = e.getMostSpecificCause().getMessage();
      if (errorMessage.contains("title")) {
        String response = "group/title-already-in-use";
        System.out.println(response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }
  }

  public ResponseEntity<Object> getGroups(String id) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isEmpty()) {
      return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
    } else {
      User user = optionalUser.get();
      List<Map<String, Object>> response = getGroupList(user);
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
  }

  public ResponseEntity<Object> getGroup(String id) {
    Optional<Group> optionalGroup = groupRepository.findById(id);
    if (optionalGroup.isEmpty()) {
      return new ResponseEntity<>("group/not-found", HttpStatus.NOT_FOUND);
    } else {
      Group group = optionalGroup.get();
      Map<String, Object> response = new HashMap<>();
      createGroup(group, response);
      Set<User> users = group.getUsers();
      Set<GroupInvitation> invitations = group.getGroupInvitations();
      List<ProfileDTO> usersList = new ArrayList<>();
      List<Map<String, Object>> invitedUsers = new ArrayList<>();
      for (User user : users) {
        ProfileDTO profile = ProfileDTO.userToProfileDTO(user);
        usersList.add(profile);
      }
      for (GroupInvitation groupInvitation : invitations) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", groupInvitation.getUser().getId());
        invitedUsers.add(map);
      }
      response.put("users", usersList);
      response.put("invitations", invitedUsers);
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
  }

  public void addUserToGroup(Group invitedTo, User notified) {
    Set<Group> userGroups = notified.getGroups();
    if (userGroups != null) {
      userGroups.add(invitedTo);
    } else {
      notified.setGroups(Set.of(invitedTo));
    } // add group to user
    Set<User> participants = invitedTo.getUsers();
    if (participants != null) {
      participants.add(notified);
    } else {
      invitedTo.setUsers(Set.of(notified));
    }
    invitedTo.setUsers(participants); // add user to group
    groupRepository.save(invitedTo); // update group
    userRepository.save(notified); // update user
  }

  public ResponseEntity<Object> removeUserFromGroupRequest(Map<String, String> groupData) {
    Optional<Group> optionalGroup = groupRepository.findById(groupData.get("group_id"));
    Optional<User> optionalUser = userRepository.findById(groupData.get("user_id"));
    if (optionalGroup.isEmpty()) {
      return new ResponseEntity<>("group/not-found", HttpStatus.NOT_FOUND);
    } else if (optionalUser.isEmpty()) {
      return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
    } else if (optionalGroup.get().getCreatedBy().equals(optionalUser.get())) {
      return new ResponseEntity<>("user/cannot-remove-creator", HttpStatus.BAD_REQUEST);
    } else if (!optionalGroup.get().getUsers().contains(optionalUser.get())) {
      return new ResponseEntity<>("user/not-in-group", HttpStatus.BAD_REQUEST);
    } else {
      Group group = optionalGroup.get();
      User user = optionalUser.get();
      removeUserFromGroup(group, user);
      return new ResponseEntity<>("User removed from group", HttpStatus.OK);
    }
  }

  public ResponseEntity<Object> joinPublicGroup(Map<String, String> groupData) {
    Optional<Group> optionalGroup = groupRepository.findById(groupData.get("group_id"));
    Optional<User> optionalUser = userRepository.findById(groupData.get("user_id"));
    if (optionalGroup.isEmpty()) {
      return new ResponseEntity<>("group/not-found", HttpStatus.NOT_FOUND);
    } else if (optionalUser.isEmpty()) {
      return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
    } else {
      Group group = optionalGroup.get();
      User user = optionalUser.get();
      if (!group.isPrivate()) {
        addUserToGroup(group, user);
        return new ResponseEntity<>("User added to group", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("group/not-public", HttpStatus.BAD_REQUEST);
      }
    }
  }

  public void removeUserFromGroup(Group group, User user) {
    Set<User> users = group.getUsers();
    users.remove(user);
    group.setUsers(users);
    Set<Group> userGroups = user.getGroups();
    userGroups.remove(group);
    user.setGroups(userGroups);
    userRepository.save(user);
    groupRepository.save(group);
  }

  public void deleteGroup(Group group) {
    groupRepository.delete(group);
  }

  public ResponseEntity<Object> deleteGroup(Map<String, String> groupInfo) {
    String group_id = groupInfo.get("group_id");
    String user_id = groupInfo.get("user_id");
    Optional<Group> groupOptional = groupRepository.findById(group_id);
    Optional<User> userOptional = userRepository.findById(user_id);
    if (groupOptional.isEmpty() && userOptional.isEmpty()) {
      return new ResponseEntity<>("group-and-user/not-found", HttpStatus.NOT_FOUND);
    }
    if (groupOptional.isEmpty()) {
      return new ResponseEntity<>("group/not-found", HttpStatus.NOT_FOUND);
    }
    if (userOptional.isEmpty()) {
      return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
    }
    User user = userOptional.get();
    Group group = groupOptional.get();
    if (group.getCreatedBy() != user) {
      return new ResponseEntity<>("user/not-owner", HttpStatus.FORBIDDEN);
    }
    deleteGroup(group);
    return new ResponseEntity<>("group/deleted", HttpStatus.OK);
  }
}
