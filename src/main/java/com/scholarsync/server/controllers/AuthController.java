package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.UserDTO;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.services.AuthService;
import com.scholarsync.server.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {


    @Autowired
    private AuthService authService;
    @Autowired
    private SessionService sessionService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDTO userDTO) {
        return authService.login(userDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody Map<String, Long> requestBody) {
        Long sessionId = requestBody.get("sessionId");
        if (sessionId != null) {
            return sessionService.refresh(sessionId);
        }
        return null;
    }
}
