package com.scholarsync.server.controllers;

import com.scholarsync.server.DataTransferProtocols.UserDTO;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.UserRepository;
import com.scholarsync.server.services.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {


    @Autowired
    private OAuthService oAuthService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User user) {
        return oAuthService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDTO userDTO) {
        return oAuthService.login(userDTO);
    }
}
