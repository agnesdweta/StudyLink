package com.example.studylink;

public class Assignment {

    private long id;
    private String title;
    private String course;
    private String deadline;
    private String image;

    public Assignment(long id, String title, String course, String deadline, String image) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.deadline = deadline;
        this.image = image;
    }

    // ===== GETTER =====
    public long getId() {
        return id;
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

    public String getImage() {
        return image;
    }

    // ===== SETTER =====
    public void setId(long id) {
        this.id = id;
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

    public void setImage(String image) {
        this.image = image;
    }
}
