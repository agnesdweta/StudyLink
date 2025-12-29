package com.example.studylink.db;

import androidx.room.*;

import java.util.List;

@Dao
public interface ExamDao {

    @Query("SELECT * FROM exams")
    List<ExamEntity> getAll();

    @Insert
    void insert(ExamEntity exam);

    @Update
    void update(ExamEntity exam);

    @Delete
    void delete(ExamEntity exam);
}
