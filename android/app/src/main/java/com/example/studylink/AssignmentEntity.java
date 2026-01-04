package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assignments")
public class AssignmentEntity {

    @PrimaryKey(autoGenerate = false)
    public long id;

    public String title;
    public String course;
    public String deadline;
    public String image;

    // 🔹 Constructor dengan parameter (opsional, tapi enak dipakai)
    public AssignmentEntity(long id, String title, String course, String deadline, String image) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.deadline = deadline;
        this.image = image;
    }

    // 🔹 Getter & Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}