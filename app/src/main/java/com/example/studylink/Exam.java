package com.example.studylink;

public class Exam {
    String title, course, date;

    public Exam(String title, String course, String date) {
        this.title = title;
        this.course = course;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getCourse() {
        return course;
    }

    public String getDate() {
        return date;
    }
}
