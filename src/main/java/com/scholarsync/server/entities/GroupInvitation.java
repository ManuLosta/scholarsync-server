package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.scholarsync.server.types.NotificationType;
import jakarta.persistence.*;

@Entity
@Table(name = "group_invitation")
@PrimaryKeyJoinColumn(name = "group_invitation_id")
public class GroupInvitation extends Notification {

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "group_id")
  Group group;

  boolean accepted;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "id")
  private User userId;

  public GroupInvitation() {
    this.accepted = false;
    this.setNotifactionType(NotificationType.GROUP_INVITE);
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public User getUserId() {
    return userId;
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
