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

      User user = userRepository.findUserById(id);

      int numAnswers = 4;    //todo implement
      int numQuestions = 20; //todo implement

      List<String> receivedRequestFromId = List.of();

      for (FriendRequest userSend : user.getReceivedFriendRequests()){

        receivedRequestFromId.add(userSend.getFrom().getId());
      }

      List<String> groupList;

      groupList = List.of("Mate amigos", "prog Austral"); // todo quit this line

      for (Group group : user.getGroups()){

        groupList.add(group.getTitle());

      }
      List<String> friendIds = new ArrayList<>();
      for (User friend : user.getFriends()) {
        friendIds.add(friend.getId());
      }

        return new ProfileDTO(receivedRequestFromId, user.getUsername(), user.getFirstName(), user.getLastName(), friendIds, user.getCredits(), numQuestions, numAnswers, groupList);

    }
}
