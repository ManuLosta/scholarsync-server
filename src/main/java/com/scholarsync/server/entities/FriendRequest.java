package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.scholarsync.server.types.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "friend_request")
@Setter
@Getter
@PrimaryKeyJoinColumn(name = "friend_request_id")
public class FriendRequest extends Notification {

  @Column(name = "accepted")
  private boolean accepted;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "from_id")
  private User from;

  @ManyToOne
  @Setter(AccessLevel.NONE)
  @JsonBackReference
  @JoinColumn(name = "to_id")
  private User to;

  public FriendRequest() {
    this.accepted = false;
    this.setNotificationType(NotificationType.FRIEND_REQUEST);
  }

  public void setTo(User to) {
    this.to = to;
    setOwner(to);
  }

  @Override
  public void setOwner(User owner) {
    super.setOwner(owner);
  }
}
