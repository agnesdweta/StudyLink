package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post")
public class PostEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String content;

    public PostEntity(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
