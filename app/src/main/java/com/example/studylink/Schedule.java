package com.example.studylink;
public class Schedule {

    String title, date, time;


    public Schedule(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }
}
