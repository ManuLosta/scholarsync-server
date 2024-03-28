package com.scholarsync.server.services;

import com.scholarsync.server.dtos.UserDTO;
import com.scholarsync.server.entities.Session;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.SessionRepository;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionService sessionService;

    public ResponseEntity<Object> register(User user) {
       Optional<User> emailEntry = userRepository.findUserByEmail(user.getEmail());
       Optional<User> usernameEntry = userRepository.findUserByUsername(user.getUsername());

        if (emailEntry.isPresent() || usernameEntry.isPresent()) {
           if (emailEntry.isPresent() && usernameEntry.isPresent()) {
               return new ResponseEntity<>("auth/email-username-already-in-use", HttpStatus.CONFLICT);
           } else if (emailEntry.isPresent()) {
               return new ResponseEntity<>("auth/email-already-in-use", HttpStatus.CONFLICT);
           } else {
               return new ResponseEntity<>("auth/username-already-in-use", HttpStatus.CONFLICT);
           }
        }

        userRepository.save(user);
        String response =  "User " + user.getFirstName() + " " + user.getLastName() + " registered successfully!";
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public ResponseEntity<Object> login(UserDTO userDTO) {
        Optional<User> optionalUser = convertToEntity(userDTO);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>("auth/user-not-found", HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();
        if (user.getPassword().equals(userDTO.getPassword())) {
            Optional<Session> optionalSession = sessionRepository.findSessionByUserId(user.getId());
            if (optionalSession.isPresent()) {
                Session session = optionalSession.get();
               if (sessionService.isSessionExpired(session.getId())) {
                   sessionService.addTime(session);
                   return new ResponseEntity<>(session.getId(), HttpStatus.OK);
               } else {
                   sessionRepository.delete(session);
               }
            }
            Session newSession = new Session();
            newSession.setUser(user);
            sessionRepository.save(newSession);
            return new ResponseEntity<>(newSession.getId(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("auth/wrong-password", HttpStatus.UNAUTHORIZED);
        }
    }

    public Optional<User> convertToEntity(UserDTO userDTO) {
        return userRepository.findUserByEmail(userDTO.getEmail());
    }
}
