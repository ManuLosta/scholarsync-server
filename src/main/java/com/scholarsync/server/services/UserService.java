package com.scholarsync.server.services;

import com.scholarsync.server.dtos.FileDTO;
import com.scholarsync.server.dtos.ProfileDTO;
import com.scholarsync.server.entities.Files;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.FileRepository;
import com.scholarsync.server.repositories.UserRepository;

import java.io.IOException;
import java.util.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

  @Autowired private UserRepository userRepository;
  @Autowired
  private FileRepository fileRepository;

  public ResponseEntity<Object> getIdByUsername(Map<String, String> username) {
    Optional<User> user = userRepository.findUserByUsername(username.get("username"));
    return user.<ResponseEntity<Object>>map(value -> ResponseEntity.ok(value.getId()))
        .orElseGet(() -> ResponseEntity.badRequest().body("user/not-found"));
  }

  public ProfileDTO getProfileInfo(String id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(ProfileDTO::userToProfileDTO).orElse(null);
  }

  @Transactional
  public ResponseEntity<Object> updateProfilePicture(MultipartFile picture, String userId)
      throws IOException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body("user/not-found");
    }
    User updatedUser = user.get();
    if (!picture.getContentType().contains("image")) {
      return ResponseEntity.badRequest().body("file/not-image");
    }
    if (picture == null) {
      return ResponseEntity.badRequest().body("file/not-found");
    }
    Files previousFile = updatedUser.getProfilePicture();
    if (previousFile != null) {
      String id = previousFile.getId();
      updatedUser.setProfilePicture(null);
      fileRepository.deleteById(id);
    }
    Files file = new Files();
    file.setFileName(picture.getOriginalFilename());
    file.setFileType(picture.getContentType());
    file.setFile(picture.getBytes());
    updatedUser.setProfilePicture(file);
    userRepository.save(updatedUser);
    return ResponseEntity.ok("profile-picture/updated");
  }

  @Transactional
  public ResponseEntity<Object> getProfilePicture(String userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body("user/not-found");
    }
    User foundUser = user.get();
    if (foundUser.getProfilePicture() == null) {
      return ResponseEntity.badRequest().body("profile-picture/not-found");
    }
    FileDTO fileDTO = FileDTO.fileToDTO(foundUser.getProfilePicture());
    HashMap<String, Object> response = new HashMap<>();
    response.put("file", fileDTO);
    response.put("base64Encoding", Base64.getEncoder().encodeToString(foundUser.getProfilePicture().getFile()));
    return ResponseEntity.ok(response);
  }
}
