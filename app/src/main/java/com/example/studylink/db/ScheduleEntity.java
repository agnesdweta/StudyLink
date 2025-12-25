package com.example.studylink.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedules")
public class ScheduleEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String date;
    private String time;

    public ScheduleEntity(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
