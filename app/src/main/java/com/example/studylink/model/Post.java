package com.example.studylink.model;

public class Post {

    private int id;
    private String title;
    private String content;

    // constructor kosong (wajib untuk Retrofit)
    public Post() {}

    // getter & setter
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
