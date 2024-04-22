package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.GroupInvitation;
import com.scholarsync.server.types.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupNotificationDTO {
  private String notification_id;
  private String group_id;
  private String title;
  private ProfileDTO owner_group;
  private ProfileDTO user_invited;

  @Setter(AccessLevel.NONE)
  private final NotificationType notificationType = NotificationType.GROUP_INVITE;

  public static GroupNotificationDTO groupInvitationToDTO(GroupInvitation groupInvitation) {
    return new GroupNotificationDTO(
        groupInvitation.getNotificationId(),
        groupInvitation.getGroup().getId(),
        groupInvitation.getGroup().getTitle(),
        ProfileDTO.userToProfileDTO(groupInvitation.getGroup().getCreatedBy()),
        ProfileDTO.userToProfileDTO(groupInvitation.getUser()));
  }
}
