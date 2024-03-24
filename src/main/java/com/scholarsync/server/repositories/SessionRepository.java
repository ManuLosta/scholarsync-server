package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    void deleteSessionById(Long sessionId);
    Optional<Session> getSessionById(Long sessionId);
    Optional<Session> findSessionByUserId(Long userId);
}
