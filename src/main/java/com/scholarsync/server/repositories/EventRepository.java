package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Event;
import com.scholarsync.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

  List<Event> findByStartBetween(LocalDateTime start, LocalDateTime end);

  List<Event> findByUserAndStartBetween(User user, LocalDateTime start, LocalDateTime end);

  List<Event> findByUser(User user);
}


