package com.example.studylink;

public class Schedule {
    private int id;
    private String title;
    private String date;
    private String time;

    // Constructor kosong → wajib untuk Retrofit/Gson
    public Schedule() {}

    // Constructor untuk create schedule (tanpa id)
    public Schedule(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }
    // Constructor lengkap dengan id → untuk Room DB / API response
    public Schedule(int id, String title, String date, String time) {
        this.id = id;
        this.title = title;
        this.date = date;
    }

        // Getter
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
}
