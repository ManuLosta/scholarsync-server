package com.scholarsync.server.dtos;

import com.scholarsync.server.types.NotificationType;

public class FriendRequesInvitationDTO {
  private String id;
  private String from;
  private String to;
  private String created_at;
  private final NotificationType notifactionType = NotificationType.FRIEND_REQUEST;

  public FriendRequesInvitationDTO(String id, String from, String to, String created_at) {
    this.id = id;
    this.from = from;
    this.to = to;
    this.created_at = created_at;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getCreated_at() {
    return created_at;
  }

  public void setCreated_at(String created_at) {
    this.created_at = created_at;
  }

    public NotificationType getNotifactionType() {
        return notifactionType;
    }
}
