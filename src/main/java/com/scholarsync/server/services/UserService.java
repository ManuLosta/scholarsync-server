package com.scholarsync.server.services;

import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.FriendRequestRepository;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;

@Service

public class UserService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Object> sendFriendRequest(Map<String, String> friendRequestBody) {
        Optional<User> fromEntry = userRepository.findById(friendRequestBody.get("from_id"));
        Optional<User> toEntry = userRepository.findUserByUsername(friendRequestBody.get("to_username"));
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


}
