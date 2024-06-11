package com.scholarsync.server.listeners;

import com.scholarsync.server.Events.CalendarEvent;
import com.scholarsync.server.dtos.EventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class CalendarEventListenerImpl implements CalendarEventListener {

  private final TaskScheduler taskScheduler;
  private final Map<String, ScheduledFuture<?>> scheduledEvents = new HashMap<>();
  private final SimpMessagingTemplate sender;

  @Autowired
  public CalendarEventListenerImpl(SimpMessagingTemplate sender, TaskScheduler taskScheduler) {
    this.sender = sender;
    this.taskScheduler = taskScheduler;
  }

  @Override
  public void onEventCreated(CalendarEvent event) {
    ScheduledFuture<?> future =
        taskScheduler.schedule(
            () -> {
              sender.convertAndSend(
                  "/individual/" + event.event().getUser().getId() + "/calendar", EventDTO.from(event));
            },
            // UTC-3
            Instant.ofEpochMilli(
                event.event().getStart().toInstant(ZoneOffset.UTC).toEpochMilli()));
    scheduledEvents.put(event.event().getId(), future);
  }

  @Override
  public void onEventDeleted(CalendarEvent event) {
    ScheduledFuture<?> taskToCancel = scheduledEvents.get(event.event().getId());
    if (taskToCancel != null) {
      taskToCancel.cancel(true);
      scheduledEvents.remove(event.event().getId());
    }
  }


}
