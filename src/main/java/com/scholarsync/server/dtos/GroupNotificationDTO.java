package com.scholarsync.server.dtos;

import com.scholarsync.server.types.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupNotificationDTO {
  private String notification_id;
  private String group_id;
  private String name;
  private String owner_group;
  private String user_invited;

  @Setter(AccessLevel.NONE)
  private final NotificationType notificationType = NotificationType.GROUP_INVITE;
}
