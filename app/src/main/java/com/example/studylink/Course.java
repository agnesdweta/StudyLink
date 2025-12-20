package com.example.studylink;

public class Course {

    private String name;
    private String sks;

    public Course(String name, String sks) {
        this.name = name;
        this.sks = sks;
    }

    public String getName() {
        return name;
    }

    public String getSks() {
        return sks;
    }
}
