package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.types.levelType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {

  public ProfileDTO() {}

  private String id;
  private String username;
  private String firstName;
  private String lastName;
  private LocalDate birthDate;
  private LocalDateTime createdAt;
  private int credits;
  private levelType level;
  private int xp;
  private List<Map<String, Object>> friends;
  private List<Map<String, Object>> groups;

  public static ProfileDTO userToProfileDTO(User user) {
    ProfileDTO profileDTO = new ProfileDTO();
    profileDTO.setId(user.getId());
    profileDTO.setUsername(user.getUsername());
    profileDTO.setFirstName(user.getFirstName());
    profileDTO.setLastName(user.getLastName());
    profileDTO.setBirthDate(user.getBirthDate());
    profileDTO.setCreatedAt(user.getCreatedAt());
    profileDTO.setCredits(user.getCredits());
    profileDTO.setLevel(user.getLevel());
    profileDTO.setXp(user.getXp());
    Set<User> friends = user.getFriends();
    Set<Group> groups = user.getGroups();
    List<Map<String, Object>> friendsList = new ArrayList<>();
    List<Map<String, Object>> groupsList = new ArrayList<>();
    Map<String, Object> friendsMap = new HashMap<>();
    Map<String, Object> groupsMap = new HashMap<>();

    for (User friend : friends) {
      Map<String, Object> friendMap = new HashMap<>();
      friendMap.put("id", friend.getId());
      friendMap.put("username", friend.getUsername());
      friendMap.put("firstName", friend.getFirstName());
      friendsList.add(friendMap);
    }

    for (Group group : groups) {
      Map<String, Object> groupMap = new HashMap<>();
      groupMap.put("id", group.getId());
      groupMap.put("title", group.getTitle());
      groupsList.add(groupMap);
    }

    profileDTO.setFriends(friendsList);
    profileDTO.setGroups(groupsList);

    return profileDTO;
  }
}
