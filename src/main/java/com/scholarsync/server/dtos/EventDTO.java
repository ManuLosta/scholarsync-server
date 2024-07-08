package com.scholarsync.server.dtos;

import com.scholarsync.server.Events.CalendarEvent;
import com.scholarsync.server.entities.Event;

import java.time.LocalDateTime;

public record EventDTO(
    String id,
    String title,
    LocalDateTime start,
    LocalDateTime end,
    ProfileDTO user,
    String groupId,
    String groupName) {

  public static EventDTO from(Event event) {
    return new EventDTO(
        event.getId(),
        event.getTitle(),
        event.getStart(),
        event.getEnd(),
        ProfileDTO.userToProfileDTO(event.getUser()),
        event.getGroup().getId(),
        event.getGroup().getTitle());
  }

  public static EventDTO from(CalendarEvent event) {
    return new EventDTO(
        event.event().getId(),
        event.event().getTitle(),
        event.event().getStart(),
        event.event().getEnd(),
        ProfileDTO.userToProfileDTO(event.event().getUser()),
        event.event().getGroup().getId(),
        event.event().getGroup().getTitle());
  }
}
