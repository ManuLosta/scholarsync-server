package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.ProfileDTO;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.services.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

  @PostMapping("/update-profile-picture")
  public ResponseEntity<Object> updateProfilePicture(
      @RequestParam(name = "picture") MultipartFile picture,
      @RequestParam(name = "user_id") String userId)
      throws IOException {
    return userService.updateProfilePicture(picture, userId);
  }

  @GetMapping("/get-profile-picture")
  public ResponseEntity<Object> getProfilePicture(@RequestParam(name = "user_id") String userId) {
    return userService.getProfilePicture(userId);
  }

  @PostMapping("/load-refresh-token")
  public ResponseEntity<Object> loadRefreshToken(@RequestBody Map<String, String> body) {
    String refreshToken = body.get("refreshToken");
    String userId = body.get("userId");
    return userService.loadRefreshToken(userId, refreshToken);
  }

  @PostMapping("/delete-refresh-token")
  public ResponseEntity<Object> deleteRefreshToken(@RequestBody Map<String, String> body) {
    String userId = body.get("userId");
    return userService.deleteRefreshToken(userId);
  }
}
