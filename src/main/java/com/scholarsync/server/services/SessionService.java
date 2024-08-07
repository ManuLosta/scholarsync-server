package com.scholarsync.server.services;

import com.scholarsync.server.dtos.ProfileDTO;
import com.scholarsync.server.entities.Session;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.SessionRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

  @Autowired private SessionRepository sessionRepository;

  public void addTime(Session session) {
    session.setExpires(session.getExpires().plusMinutes(10));
    sessionRepository.save(session);
  }

  public ResponseEntity<Object> refresh(String sessionId) {
    Optional<Session> session = sessionRepository.getSessionById(sessionId);
    if (session.isPresent()) {
      if (session.get().getExpires().isAfter(LocalDateTime.now())) {
        User user = session.get().getUser();
        String refreshToken = user.getGoogleRefreshToken();
        ProfileDTO profileDTO = ProfileDTO.userToProfileDTO(user);
        Map<String, Object> response = new HashMap<>();
        response.put("refresh_token", Objects.requireNonNullElse(refreshToken, ""));
        response.put("profile", profileDTO);
        return ResponseEntity.ok(response);
      } else {
        sessionRepository.delete(session.get());
        return new ResponseEntity<>("auth/unauthorized", HttpStatus.UNAUTHORIZED);
      }
    }
    return null;
  }

  public boolean isSessionExpired(String sessionId) {
    Optional<Session> session = sessionRepository.getSessionById(sessionId);
    return session.isEmpty() || session.get().getExpires().isBefore(LocalDateTime.now());
  }
}
