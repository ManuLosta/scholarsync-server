package com.scholarsync.server.controllers;




import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/get-id-by-username")
    public ResponseEntity<Object> getIdByUsernames(@RequestBody Map<String,String> username){
        return userService.getIdByUsername(username);
    }


}