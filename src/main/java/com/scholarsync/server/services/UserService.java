package com.scholarsync.server.services;

import com.scholarsync.server.dtos.ProfileDTO;
import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.UserRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired private UserRepository userRepository;

  public ResponseEntity<Object> getIdByUsername(Map<String, String> username) {
    Optional<User> user = userRepository.findUserByUsername(username.get("username"));
    return user.<ResponseEntity<Object>>map(value -> ResponseEntity.ok(value.getId()))
        .orElseGet(() -> ResponseEntity.badRequest().body("user/not-found"));
  }

  public ProfileDTO getProfileInfo(String id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(UserService::userToProfileDTO).orElse(null);


  }

  private static ProfileDTO userToProfileDTO(User user) {
    ProfileDTO profileDTO = new ProfileDTO();
    profileDTO.setId(user.getId());
    profileDTO.setUsername(user.getUsername());
    profileDTO.setFirstName(user.getFirstName());
    profileDTO.setLastName(user.getLastName());
    profileDTO.setBirthDate(user.getBirthDate());
    profileDTO.setCreatedAt(user.getCreatedAt());
    profileDTO.setCredits(user.getCredits());
    Set<User> friends = user.getFriends();
    Set<Group> groups = user.getGroups();
    Map<String,Object> friendsMap = new HashMap<>();
    Map<String,Object> groupsMap = new HashMap<>();

    for(User friend : friends) {
      friendsMap.put(friend.getId(), friend.getUsername());
    }

    for(Group group : groups) {
      groupsMap.put(group.getId(), group.getTitle());
    }

    profileDTO.setFriends(friendsMap);
    profileDTO.setGroups(groupsMap);

    return profileDTO;
  }
}
