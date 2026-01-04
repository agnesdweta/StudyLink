package com.example.studylink.model;

public class LoginResponse {

    private String message;
    private String token;
    private String username;
    private long id;
    public long getUserId() { return id; }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
