package com.example.studylink;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ExamEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String course;
    private String examType;
    private String examDate;

    public ExamEntity(String course, String examType, String examDate) {
        this.course = course;
        this.examType = examType;
        this.examDate = examDate;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public String getExamDate() { return examDate; }
    public void setExamDate(String examDate) { this.examDate = examDate; }
}
