package com.scholarsync.server.services;

import com.scholarsync.server.entities.Session;
import com.scholarsync.server.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public void addTime(Session session) {
        session.setExpires(session.getExpires().plusMinutes(10));
        sessionRepository.save(session);
    }
    /**
     *
     * Me suena raro. Con que recibas el id de la sesión es suficiente.
     * Tambien habria que validar que la sesion pertenezca al usuario que esta intentando acceder.
     *
     */
    public ResponseEntity<Object> refresh(String sessionId) {
        Optional<Session> session = sessionRepository.getSessionById(sessionId);
        if (session.isPresent()) {
            if (session.get().getExpires().isAfter(LocalDateTime.now())) {
                addTime(session.get());
                return new ResponseEntity<>(session.get().getUser(), HttpStatus.OK);
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
