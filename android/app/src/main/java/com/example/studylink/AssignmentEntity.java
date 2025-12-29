package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assignments")
public class AssignmentEntity {

    @PrimaryKey
    public int id;

    public String title;
    public String course;
    public String deadline;

    // 🔹 Constructor dengan parameter (opsional, tapi enak dipakai)
    public AssignmentEntity(int id, String title, String course, String deadline) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.deadline = deadline;
    }

    // 🔹 Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
