package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "forum")
public class ForumEntity {

    @PrimaryKey
    private long id;

    private String content;
    private String user;
    private String createdAt;

    public ForumEntity() {}

    // Konstruktor tanpa id → untuk insert Room
    public ForumEntity(long id, String content, String user, String createdAt) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
