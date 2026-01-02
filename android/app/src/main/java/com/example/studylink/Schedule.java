package com.example.studylink;

public class Schedule {
    private long id; // ubah dari int ke long
    private String title;
    private String date;
    private String time;

    public Schedule(long id, String title, String date, String time) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
