package com.scholarsync.server.services;

import com.scholarsync.server.dtos.LoginDTO;
import com.scholarsync.server.dtos.RegisterDTO;
import com.scholarsync.server.entities.Session;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.SessionRepository;
import com.scholarsync.server.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  @Autowired private UserRepository userRepository;

  @Autowired private SessionRepository sessionRepository;

  @Autowired private SessionService sessionService;

  public ResponseEntity<Object> register(RegisterDTO user) {
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

    User newUser = new User();
    newUser.setFirstName(user.getFirstName());
    newUser.setLastName(user.getLastName());
    newUser.setEmail(user.getEmail());
    newUser.setUsername(user.getUsername());
    newUser.setPassword(user.getPassword());
    newUser.setBirthDate(user.getBirthDate());
    userRepository.save(newUser);
    String response =
        "User "
            + newUser.getFirstName()
            + " "
            + newUser.getLastName()
            + " registered successfully!";
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  public ResponseEntity<Object> login(LoginDTO userDTO) {
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
          sessionRepository.delete(session);
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

  public ResponseEntity<Object> logout(String sessionId) {
    Optional<Session> session = sessionRepository.getSessionById(sessionId);
    if (session.isPresent()) {
      sessionRepository.delete(session.get());
      return new ResponseEntity<>("auth/logout-success", HttpStatus.OK);
    }
    return new ResponseEntity<>("auth/session-not-found", HttpStatus.NOT_FOUND);
  }

  public Optional<User> convertToEntity(LoginDTO userDTO) {
    return userRepository.findUserByEmail(userDTO.getEmail());
  }
}
