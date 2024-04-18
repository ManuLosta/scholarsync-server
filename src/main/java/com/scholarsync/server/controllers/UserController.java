package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.ProfileDTO;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.services.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  @Autowired private UserRepository userRepository;
  @Autowired private UserService userService;

  @GetMapping
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @PostMapping("/get-id-by-username")
  public ResponseEntity<Object> getIdByUsernames(@RequestBody Map<String, String> username) {
    return userService.getIdByUsername(username);
  }

  @GetMapping("/profile/{id}")
  public ProfileDTO getProfileInfo(@PathVariable String id) {
    return userService.getProfileInfo(id);
  }
}
