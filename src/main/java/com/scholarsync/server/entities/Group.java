package com.scholarsync.server.entities;


import jakarta.persistence.*;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
public class Group {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Id
    @Column(name = "group_id", unique = true)
    String id;


    @Column(name = "title", unique = true)
    String title;

    @Column(name = "description")

    String description;

    @Column(name = "isPrivate")

    boolean isPrivate;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany(mappedBy = "groups")
    Set<User> users = new HashSet<>();






    public Group() {
        this.id = String.valueOf((Math.abs(secureRandom.nextLong())));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }



    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}
