package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.scholarsync.server.types.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "group_invitation")
@PrimaryKeyJoinColumn(name = "group_invitation_id")
public class GroupInvitation extends Notification {

  @Setter
  @Getter
  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "group_id")
  Group group;

  @Setter boolean accepted;

  @Getter
  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "id")
  private User userId;

  public GroupInvitation() {
    this.accepted = false;
    this.setNotificationType(NotificationType.GROUP_INVITE);
  }

  public void setUserId(User userId) {
    this.userId = userId;
    setOwner(userId);
  }

  @Override
  public void setOwner(User owner) {
    super.setOwner(owner);
  }
}
