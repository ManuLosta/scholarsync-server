package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Session;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
  void deleteSessionById(String sessionId);

  Optional<Session> getSessionById(String sessionId);

  Optional<Session> findSessionByUserId(String userId);
}
