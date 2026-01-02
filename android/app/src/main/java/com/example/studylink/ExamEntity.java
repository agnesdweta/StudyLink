package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exams")
public class ExamEntity {

    @PrimaryKey
    private long id;

    private String title;
    private String course;
    private String date;
    private String time;

    public ExamEntity(long id, String title, String course, String date, String time) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.date = date;
        this.time = time;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getCourse() { return course; }
    public String getDate() { return date; }
    public String getTime() { return time; }

    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCourse(String course) { this.course = course; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
}
