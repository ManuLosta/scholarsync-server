package com.scholarsync.server.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String id;

    @CreationTimestamp
    @Column(name = "created_at")

    private LocalDateTime created;

    @Column(name = "expires_at")
    private LocalDateTime expires;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    public Session() {
        this.expires = LocalDateTime.now().plusHours(1);
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
