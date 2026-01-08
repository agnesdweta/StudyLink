package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_items")
public class SearchEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;   // ⬅️ PAKAI long (AMAN)

    public String title;
    public String description;

    public SearchEntity(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
