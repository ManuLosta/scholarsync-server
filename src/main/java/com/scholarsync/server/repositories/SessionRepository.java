package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    void deleteSessionById(String sessionId);
    Optional<Session> getSessionById(String sessionId);
    Optional<Session> findSessionByUserId(String userId);
}
