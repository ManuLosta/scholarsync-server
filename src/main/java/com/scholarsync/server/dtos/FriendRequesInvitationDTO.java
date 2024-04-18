package com.scholarsync.server.dtos;

import com.scholarsync.server.types.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FriendRequesInvitationDTO {
  private String id; //
  private String from; //
  private String to; //
  private String created_at; //

  @Setter(AccessLevel.NONE)
  private final NotificationType notifactionType = NotificationType.FRIEND_REQUEST; // only getter

  public FriendRequesInvitationDTO(String id, String from, String to, String created_at) {
    this.id = id;
    this.from = from;
    this.to = to;
    this.created_at = created_at;
  }
}
