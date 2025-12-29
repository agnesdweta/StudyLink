package com.example.studylink;

public class Course {

    private int id;
    private String name;
    private String description;
    private String time;
    private String instructor;

    public Course(int id, String name, String description, String time, String instructor) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
        this.instructor = instructor;
    }

    // ===== GETTER =====
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public String getInstructor() {
        return instructor;
    }

    // ===== SETTER =====
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
}
