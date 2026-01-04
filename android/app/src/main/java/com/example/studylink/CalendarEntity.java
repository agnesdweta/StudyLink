package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "calendar")
public class CalendarEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String date;        // format yyyy-MM-dd
    private String title;       // judul agenda
    private String description; // isi agenda
    private String user;        // user yang membuat agenda

    // Constructor kosong (dibutuhkan Room)
    public CalendarEntity() {}

    // Constructor lengkap
    public CalendarEntity(String date, String title, String description, String user) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.user = user;
    }

    // Getter & Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
}
