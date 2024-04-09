package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.scholarsync.server.types.NotificationType;
import jakarta.persistence.*;

@Entity
@Table(name = "friend_request")
@PrimaryKeyJoinColumn(name = "friend_request_id")
public class FriendRequest extends Notification {

  @Column(name = "accepted")
  private boolean accepted;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "from_id")
  private User from;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "to_id")
  private User to;

  public FriendRequest() {
    this.accepted = false;
    this.setNotifactionType(NotificationType.FRIEND_REQUEST);
  }

  public boolean isAccepted() {
    return accepted;
  }

  public void setAccepted(boolean accepted) {
    this.accepted = accepted;
  }

  public User getFrom() {
    return from;
  }

  public void setFrom(User from) {
    this.from = from;
  }

  public User getTo() {
    return to;
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
