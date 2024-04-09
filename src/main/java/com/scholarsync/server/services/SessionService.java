package com.scholarsync.server.services;

import com.scholarsync.server.dtos.RefreshDTO;
import com.scholarsync.server.entities.Session;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.SessionRepository;
import java.time.LocalDateTime;
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
        addTime(session.get());
        User user = session.get().getUser();
        RefreshDTO refreshDTO = new RefreshDTO();
        refreshDTO.setId(user.getId());
        refreshDTO.setFirstName(user.getFirstName());
        refreshDTO.setLastName(user.getLastName());
        refreshDTO.setUsername(user.getUsername());
        return new ResponseEntity<>(refreshDTO, HttpStatus.OK);
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
