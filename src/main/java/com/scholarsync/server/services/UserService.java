package com.scholarsync.server.services;

import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.FriendRequestRepository;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class UserService {

    @Autowired
    private UserRepository userRepository;



    public ResponseEntity<Object> getIdByUsername(Map<String, String> username) {
        Optional<User> user = userRepository.findUserByUsername(username.get("username"));
        return user.<ResponseEntity<Object>>map(value -> ResponseEntity.ok(value.getId())).orElseGet(() -> ResponseEntity.badRequest().body("user/not-found"));
    }


}
