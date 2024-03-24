package com.scholarsync.server.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Entity
public class Session {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Column(unique = true)
    private long id;

    @Id
    @Column(name = "userId")
    private long userId;
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
        this.expires = this.created.plusSeconds(60);
        this.id = generateSessionId();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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
