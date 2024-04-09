package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "credits")
    private int credits;

    @Column(name = "xp")
    private int xp;

    @Column(name = "level_id")
    private long levelId = 0;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonIgnoreProperties({"createdBy", "users"})
    private Set<Group> owner;

    @ManyToMany
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @JsonIgnoreProperties("users")
    private Set<Group> groups;

    @ManyToMany
    @JoinTable(
            name = "friend_with",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")

    )
    private Set<User> friends;


    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Notification> receivedNotifications;

    @OneToMany(mappedBy = "from", cascade = CascadeType.ALL)
    private Set<FriendRequest> sentFriendRequests;

    @OneToMany(mappedBy = "to", cascade = CascadeType.ALL)
    private Set<FriendRequest> receivedFriendRequests;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public long getLevelId() {
        return levelId;
    }

    public void setLevelId(long levelId) {
        this.levelId = levelId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Group> getOwner() {
        return owner;
    }

    public void setOwner(Set<Group> owner) {
        this.owner = owner;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public Set<Notification> getReceivedNotifications() {
        return receivedNotifications;
    }

    public void setReceivedNotifications(Set<Notification> receivedNotifications) {
        this.receivedNotifications = receivedNotifications;
    }

    public Set<FriendRequest> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void setSentFriendRequests(Set<FriendRequest> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
    }

    public Set<FriendRequest> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public void setReceivedFriendRequests(Set<FriendRequest> receivedFriendRequests) {
        this.receivedFriendRequests = receivedFriendRequests;
    }


}