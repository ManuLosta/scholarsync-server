package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.EventInputDTO;
import com.scholarsync.server.dtos.EventModifyDTO;
import com.scholarsync.server.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

  @Autowired EventService eventService;

  @GetMapping
  public ResponseEntity<Object> getEvents(@RequestParam String userId) {
    return eventService.getUserEvents(userId);
  }

  @PostMapping("/create")
  public ResponseEntity<Object> createEvent(@RequestBody EventInputDTO event) {
    return eventService.createEvent(event);
  }

  @PostMapping("/update")
  public ResponseEntity<Object> updateEvent(@RequestBody EventModifyDTO eventUpdate) {
    String id = eventUpdate.id();
    String title = eventUpdate.title();
    LocalDateTime start = eventUpdate.start();
    LocalDateTime end = eventUpdate.end();
    return eventService.updateEvent(id, title, start, end);
  }

  @PostMapping("/delete")
  public ResponseEntity<Object> deleteEvent(@RequestBody String eventId) {
    return eventService.deleteEvent(eventId);
  }
}
