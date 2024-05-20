package com.scholarsync.server.webSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class WebSocketNotificationService {

  private final SimpMessagingTemplate template;

  @Autowired
  public WebSocketNotificationService(SimpMessagingTemplate template) {
    this.template = template;
  }

  public void sendNotification(String user, CustomNotificationDTO customNotification) {
    template.convertAndSendToUser(user, "/notification", customNotification);
  }

  @Scheduled(fixedRate = 3000)
  public void sendPingToAllUsers() {
    System.out.println("Sending ping to all users");
    template.convertAndSend("/global/ping", "ping");
  }
}