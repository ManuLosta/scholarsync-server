package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.LoginDTO;
import com.scholarsync.server.dtos.RegisterDTO;
import com.scholarsync.server.services.AuthService;
import com.scholarsync.server.services.SessionService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  @Autowired private AuthService authService;
  @Autowired private SessionService sessionService;

  @PostMapping("/register")
  public ResponseEntity<Object> register(@RequestBody RegisterDTO user) {
    return authService.register(user);
  }

  @PostMapping("/login")
  public ResponseEntity<Object> login(@RequestBody LoginDTO userDTO) {
    return authService.login(userDTO);
  }

  @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestBody Map<String, String> requestBody) {
        return authService.logout(requestBody.get("sessionId"));
    }

  @PostMapping("/refresh")
  public ResponseEntity<Object> refresh(@RequestBody Map<String, String> requestBody) {
    String sessionId = requestBody.get("sessionId");
    if (!sessionId.isEmpty()) {
      return sessionService.refresh(sessionId);
    }
    return null;
  }
}
