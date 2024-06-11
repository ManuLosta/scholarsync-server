package com.scholarsync.server.listeners;

import com.scholarsync.server.Events.CalendarEvent;
import org.springframework.context.event.EventListener;

public interface CalendarEventListener {
  @EventListener
  void onEventCreated(CalendarEvent event);

  @EventListener
  void onEventDeleted(CalendarEvent event);

}
