package com.scholarsync.server.dtos;

import com.scholarsync.server.types.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class liveNotificationDTO {

  NotificationType notificationType;

  String groupId;

  String from;

  String to;

  String message = "fetch again";
}
