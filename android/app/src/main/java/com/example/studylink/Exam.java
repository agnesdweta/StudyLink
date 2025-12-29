package com.example.studylink;

public class Exam {
    private int id;
    private String title;
    private String course;
    private String date;

    public Exam() {}

    public Exam(String title, String course, String date) {
        this.title = title;
        this.course = course;
        this.date = date;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCourse() { return course; }
    public String getDate() {
        return date;}

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

