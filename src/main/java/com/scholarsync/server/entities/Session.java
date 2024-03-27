package com.scholarsync.server.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Entity
public class Session {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Column(unique = true)
    private String id;

    @Id
    @Column(name = "userId")
    private String userId;
    @CreationTimestamp
    @Column(name = "created_at")

    private LocalDateTime created;

    @Column(name = "expires_at")

    private LocalDateTime expires;
    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    private User user;

    public Session(){
        this.created = LocalDateTime.now();
        this.expires = this.created.plusHours(1);
        this.id = String.valueOf(generateSessionId());
    }

    private static long generateSessionId(){
        return Math.abs(secureRandom.nextLong());
    }


    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }
}
