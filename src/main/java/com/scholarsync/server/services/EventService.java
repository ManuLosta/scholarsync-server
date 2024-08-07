package com.scholarsync.server.services;

import com.scholarsync.server.Events.CalendarEvent;
import com.scholarsync.server.dtos.EventDTO;
import com.scholarsync.server.dtos.EventInputDTO;
import com.scholarsync.server.entities.Event;
import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.listeners.CalendarEventListenerImpl;
import com.scholarsync.server.repositories.EventRepository;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Optional;

@Service
public class EventService {

  @Autowired private CalendarEventListenerImpl calendarEventListener;

  @Autowired private EventRepository eventRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;

  @Transactional
  public ResponseEntity<Object> createEvent(EventInputDTO eventInput) {

    Result<User> userResult;
    Result<Group> groupResult;

    Event event = new Event();
    event.setTitle(eventInput.title());
    event.setStart(eventInput.start());
    event.setEnd(eventInput.end());
    String userId = eventInput.userId();
    String groupId = eventInput.groupId();


    if (checkUser(userId).success) userResult = checkUser(userId);
    else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");

    if (checkGroup(groupId).success) groupResult = checkGroup(groupId);
    else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("group/not-found");

    if (!checkDates(eventInput.start(), eventInput.end()).success)
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("dates/invalid");

    if (!checkDates(LocalDateTime.now(), eventInput.start()).success)
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("dates/invalid");

    event.setGroup(groupResult.value);
    event.setUser(userResult.value);

    eventRepository.save(event);

    calendarEventListener.onEventCreated(new CalendarEvent(event));

    EventDTO response = EventDTO.from(event);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  public ResponseEntity<Object> deleteEvent(String eventId) {
    Optional<Event> event = eventRepository.findById(eventId);
    if (event.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event/not-found");
    calendarEventListener.onEventDeleted(new CalendarEvent(event.get()));
    eventRepository.delete(event.get());
    return ResponseEntity.status(HttpStatus.OK).body("event/deleted");
  }

  public ResponseEntity<Object> updateEvent(
      String eventId, String title, LocalDateTime start, LocalDateTime end) {
    Optional<Event> event = eventRepository.findById(eventId);
    if (event.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("event/not-found");
    calendarEventListener.onEventDeleted(new CalendarEvent(event.get()));
    Event updatedEvent = event.get();
    updatedEvent.setTitle(title);
    updatedEvent.setStart(start);
    updatedEvent.setEnd(end);
    eventRepository.save(updatedEvent);
    calendarEventListener.onEventCreated(new CalendarEvent(updatedEvent));
    EventDTO response = EventDTO.from(updatedEvent);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  public ResponseEntity<Object> getUserEvents(String userId) {
    Result<User> userResult;
    if (checkUser(userId).success) userResult = checkUser(userId);
    else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");

    return ResponseEntity.status(HttpStatus.OK)
        .body(eventRepository.findByUser(userResult.value).stream().map(EventDTO::from));
  }

  Result<User> checkUser(String userId) {
    Optional<User> user = userRepository.findById(userId);
    return (user.isEmpty()) ? new Result<>(false, null) : new Result<>(true, user.get());
  }

  Result<Group> checkGroup(String groupId) {
    Optional<Group> group = groupRepository.findById(groupId);
    return (group.isEmpty()) ? new Result<>(false, null) : new Result<>(true, group.get());
  }

  Result<Object> checkDates(LocalDateTime start, LocalDateTime end) {
    return (start.isAfter(end)) ? new Result<>(false, null) : new Result<>(true, null);
  }

  record Result<T>(boolean success, T value) {}

  private enum ChangeType {
    TITLE,
    START,
    END
  }
}
