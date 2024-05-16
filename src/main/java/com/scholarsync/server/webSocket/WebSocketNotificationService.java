package com.scholarsync.server.webSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
}
