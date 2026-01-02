package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "forum")
public class Forum {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String content;

    public Forum(String content) {
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
