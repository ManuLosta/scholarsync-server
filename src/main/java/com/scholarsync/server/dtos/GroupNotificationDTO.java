package com.scholarsync.server.dtos;

import com.scholarsync.server.types.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupNotificationDTO {
  private String id;
  private String name;
  private String ownerName;

  @Setter(AccessLevel.NONE)
  private final NotificationType notifactionType = NotificationType.GROUP_INVITE;
}
