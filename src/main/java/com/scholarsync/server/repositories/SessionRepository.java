package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session,Long> {
}
