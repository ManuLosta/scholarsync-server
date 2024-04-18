package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.scholarsync.server.types.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "group_invitation")
@PrimaryKeyJoinColumn(name = "group_invitation_id")
@Getter
@Setter
public class GroupInvitation extends Notification {

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "group_id")
  Group group;

  boolean accepted;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "id")
  @Setter(AccessLevel.NONE)
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
