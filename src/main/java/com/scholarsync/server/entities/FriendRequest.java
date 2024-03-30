package com.scholarsync.server.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "friend_request")

public class FriendRequest {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(name = "request_id", unique = true)
    private String id;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    private boolean accepted;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private User from;

    @ManyToOne
    @JoinColumn(name = "to_id")
    private User to;

    public FriendRequest() {
        this.id = String.valueOf((Math.abs(secureRandom.nextLong())));
        this.createdAt = LocalDateTime.now();
        this.accepted = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
    }

}
