package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class CourseEntity {

    @PrimaryKey
    private long id; // ⬅️ HARUS long & TANPA autoGenerate

    private String name;
    private String description;
    private String time;
    private String instructor;

    public CourseEntity() {}

    public CourseEntity(long id, String name, String description, String time, String instructor) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
        this.instructor = instructor;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
}
