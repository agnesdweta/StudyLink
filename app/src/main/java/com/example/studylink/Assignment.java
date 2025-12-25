package com.example.studylink;

public class Assignment {

    String title;
    String course;
    String deadline;

    public Assignment(String title, String course, String deadline) {
        this.title = title;
        this.course = course;
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public String getCourse() {
        return course;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setCourse(String course) {
        this.course = course;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

}
