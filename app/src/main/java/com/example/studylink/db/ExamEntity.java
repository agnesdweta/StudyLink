package com.example.studylink.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exams")
public class ExamEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String course;
    public String date;

    public ExamEntity(String title, String course, String date) {
        this.title = title;
        this.course = course;
        this.date = date;
    }
}
