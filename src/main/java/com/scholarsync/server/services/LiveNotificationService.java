package com.scholarsync.server.services;

import com.scholarsync.server.dtos.FriendRequestInvitationDTO;
import com.scholarsync.server.dtos.GroupNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class LiveNotificationService {

  private final SimpMessagingTemplate template;

  @Autowired
  public LiveNotificationService(SimpMessagingTemplate template) {
    this.template = template;
  }

  public void sendFriendNotification(String sessionId, FriendRequestInvitationDTO customNotification) {
    System.out.println("sending notification to user");
    this.template.convertAndSend("/individual/" + sessionId + "/notification", customNotification);
  }

  public void sendGroupNotification(String sessionId, GroupNotificationDTO customNotification) {
    System.out.println("sending notification to user");
    this.template.convertAndSend("/individual/" + sessionId + "/notification", customNotification);
  }
}