package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.UserDTO;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {


    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDTO userDTO) {
        return authService.login(userDTO);
    }

    @PostMapping("/validate")
    public ResponseEntity<Object> validate(@RequestBody Map<String, Long> requestBody) {
        Long sessionId = requestBody.get("sessionId");
        if (sessionId != null) {
            return authService.validate(sessionId);
        }
        return null;
    }
}
