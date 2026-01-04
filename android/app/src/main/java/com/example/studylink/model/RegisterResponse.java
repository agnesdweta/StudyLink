package com.example.studylink.model;

public class RegisterResponse {
    private String message;
    private String username;
    private String token;
    private long id;

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
    public long getUserId() { return id; }
}
