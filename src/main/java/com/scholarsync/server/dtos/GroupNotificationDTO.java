package com.scholarsync.server.dtos;

import com.scholarsync.server.types.NotificationType;

public class GroupNotificationDTO {
  private String id;
  private String name;
  private String ownerName;
  private final NotificationType notifactionType = NotificationType.GROUP_INVITE;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public NotificationType getNotifactionType() {
    return notifactionType;
  }
}
