package com.scholarsync.server.services;

import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.FriendRequestRepository;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class UserService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private UserRepository userRepository;

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

        if(friendRequestRepository.existsByFromAndTo(fromEntry.get(), toEntry.get())){
            return ResponseEntity.badRequest().body("friend-request/already-sent");
        }
        friendRequestRepository.save(friendRequest);

        return ResponseEntity.ok("friend-request/sent");

    }

    public ResponseEntity<Object> getIdByUsername(Map<String, String> username) {
        Optional<User> user = userRepository.findUserByUsername(username.get("username"));
        return user.<ResponseEntity<Object>>map(value -> ResponseEntity.ok(value.getId())).orElseGet(() -> ResponseEntity.badRequest().body("user/not-found"));
    }

    public ResponseEntity<Object> getAllRequests(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("user/not-found");
        }
        List<Map<String,Object>> response = new ArrayList<>();
        for (FriendRequest friendRequest : user.get().getReceivedRequests()) {
            response.add(Map.of(
                    "id", friendRequest.getId(),
                    "from", friendRequest.getFrom().getUsername(),
                    "to", friendRequest.getTo().getUsername(),
                    "created_at", friendRequest.getCreatedAt()
            ));
        }
        return ResponseEntity.ok(response);
    }

    public void deleteFriendRequest(String idRequest){

        FriendRequest friendRequest = friendRequestRepository.findFriendRequestById(idRequest);
        friendRequestRepository.delete(friendRequest);
    }

    public List<User> addFriend(String idRequest){
        FriendRequest friendRequest = friendRequestRepository.findFriendRequestById(idRequest);
        User friend1 = friendRequest.getTo();
        User friend2 = friendRequest.getFrom();

        Set<User> usersFriend1 = new HashSet<>(friend1.getFriends());
        usersFriend1.add(friend2);
        friend1.setFriends(usersFriend1);

        deleteFriendRequest(idRequest);
        return List.of(friend1, friend2);

    }



}
