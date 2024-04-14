package com.scholarsync.server.dtos;

import java.util.List;

public class ProfileDTO {
    public ProfileDTO(String username, String firstName, String lastName, int friends, int credits, int questions, int answer, List<String> groups) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = friends;
        this.credits = credits;
        this.questions = questions;
        this.answer = answer;
        this.groups = groups;
    }

    private String username;
    private String firstName;
    private String lastName;
    private int friends;
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

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
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
