package com.scholarsync.server.controllers;

import com.scholarsync.server.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
  @Autowired private NotificationService notificationService;

  @GetMapping("/get-notifications/{userId}")
  public ResponseEntity<Object> getAllNotifications(@PathVariable String userId) {
    return notificationService.getAllNotifications(userId);
  }
}
