package com.scholarsync.server.dtos;

import java.util.List;

public class ProfileDTO {
    public ProfileDTO(List<String> receivedFriendsRequest, String username, String firstName, String lastName, List<String> friends, int credits, int questions, int answer, List<String> groups) {
        this.receivedFriendsRequest = receivedFriendsRequest;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = friends;
        this.credits = credits;
        this.questions = questions;
        this.answer = answer;
        this.groups = groups;
    }

    public List<String> getReceivedFriendsRequest() {
        return receivedFriendsRequest;
    }

    public void setReceivedFriendsRequest(List<String> receivedFriendsRequest) {
        this.receivedFriendsRequest = receivedFriendsRequest;
    }

    private List<String> receivedFriendsRequest;
    private String username;
    private String firstName;
    private String lastName;
    private List<String> friends;
    private int credits;
    private int questions;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        friends = friends;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    private int answer;
    private List<String> groups;


}
