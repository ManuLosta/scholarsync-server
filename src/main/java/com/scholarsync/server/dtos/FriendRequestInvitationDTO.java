package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.FriendRequest;
import com.scholarsync.server.types.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FriendRequestInvitationDTO {
  private String notification_id;
  private ProfileDTO from;
  private ProfileDTO to;
  private String created_at;

  @Setter(AccessLevel.NONE)
  private final NotificationType notificationType = NotificationType.FRIEND_REQUEST; // only getter

  public static FriendRequestInvitationDTO friendRequestToDTO(FriendRequest friendRequest) {
    return new FriendRequestInvitationDTO(
        friendRequest.getNotificationId(),
        ProfileDTO.userToProfileDTO(friendRequest.getFrom()),
        ProfileDTO.userToProfileDTO(friendRequest.getTo()),
        friendRequest.getCreatedAt().toString());
  }
}
